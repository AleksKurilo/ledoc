package dk.ledocsystem.ledoc.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.ledoc.config.security.JwtTokenService;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsDTO;
import dk.ledocsystem.ledoc.dto.review.ReviewDTO;
import dk.ledocsystem.ledoc.dto.review.ReviewQuestionAnswerDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.EmployeeDetails;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.model.review.EmployeeReview;
import dk.ledocsystem.ledoc.model.review.EmployeeReviewQuestionAnswer;
import dk.ledocsystem.ledoc.model.review.ReviewQuestion;
import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.repository.EmployeeReviewRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.service.ReviewQuestionService;
import dk.ledocsystem.ledoc.service.ReviewTemplateService;
import dk.ledocsystem.ledoc.service.dto.EmployeePreviewDTO;
import dk.ledocsystem.ledoc.service.dto.GetEmployeeDTO;
import dk.ledocsystem.ledoc.service.exceptions.ReviewNotApplicableException;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import dk.ledocsystem.ledoc.validator.EmployeeCreateDtoValidator;
import dk.ledocsystem.ledoc.validator.EmployeeDtoValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final EmployeeRepository employeeRepository;
    private final LocationService locationService;
    private final ReviewTemplateService reviewTemplateService;
    private final ReviewQuestionService reviewQuestionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final EmailNotificationRepository emailNotificationRepository;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final ModelMapper modelMapper;
    private final EmployeeDtoValidator employeeDtoValidator;
    private final EmployeeCreateDtoValidator employeeCreateDtoValidator;
    private final BaseValidator<ReviewDTO> reviewDtoBaseValidator;


    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull Customer customer) {
        employeeCreateDtoValidator.validate(employeeCreateDTO);

        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(customer);

        Employee responsible = resolveResponsible(employeeCreateDTO.getResponsibleId());
        employee.setResponsible(responsible);

        Set<Location> locations = resolveLocations(employeeCreateDTO.getLocationIds());
        employee.setLocations(locations);

        if (employeeDetailsPresent(employeeCreateDTO)) {
            updateReviewDetails(employeeCreateDTO.getDetails(), employee.getDetails());
        }

        employee = employeeRepository.save(employee);

        addAuthorities(employee, employeeCreateDTO);
        sendMessages(employeeCreateDTO, responsible);
        return employee;
    }

    private void addAuthorities(Employee employee, EmployeeCreateDTO employeeCreateDTO) {
        String roleString = ObjectUtils.defaultIfNull(employeeCreateDTO.getRole(), "user");
        addAuthorities(employee, UserAuthorities.fromString(roleString));

        if (employeeCreateDTO.isCanCreatePersonalLocation()) {
            addAuthorities(employee, UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }
    }

    @Transactional
    @Override
    public Employee createPointOfContact(@NonNull EmployeeCreateDTO employeeCreateDTO, Customer customer) {
        Employee poc = createEmployee(employeeCreateDTO, customer);
        addAuthorities(poc, UserAuthorities.SUPER_ADMIN);
        return poc;
    }

    @Transactional
    @Override
    public Employee updateEmployee(@NonNull EmployeeDTO employeeDTO) {
        employeeDtoValidator.validate(employeeDTO);
        Employee employee = employeeRepository.findById(employeeDTO.getId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeDTO.getId().toString()));

        modelMapper.map(employeeDTO, employee);

        Long responsibleId = employeeDTO.getResponsibleId();
        if (responsibleChanged(employee.getResponsible(), responsibleId)) {
            Employee responsible = resolveResponsible(responsibleId);
            employee.setResponsible(responsible);
            if (responsible != null) {
                sendNotificationToResponsible(responsible);
            }
        }

        employee.setLocations(resolveLocations(employeeDTO.getLocationIds()));

        if (employeeDetailsPresent(employeeDTO)) {
            updateReviewDetails(employeeDTO.getDetails(), employee.getDetails());
        }

        updateAuthorities(employee, employeeDTO);
        return employeeRepository.save(employee);
    }

    private boolean employeeDetailsPresent(EmployeeDTO employeeDTO) {
        return employeeDTO.getDetails() != null;
    }

    private void updateReviewDetails(EmployeeDetailsDTO detailsDTO, EmployeeDetails employeeDetails) {
        if (detailsDTO.isSkillAssessed()) {
            Long skillResponsibleId = detailsDTO.getSkillResponsibleId();
            employeeDetails.setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));

            Long reviewTemplateId = detailsDTO.getReviewTemplateId();
            employeeDetails.setReviewTemplate(resolveReviewTemplate(reviewTemplateId));
        } else {
            employeeDetails.eraseReviewDetails();
        }
    }

    private boolean responsibleChanged(Employee oldResponsible, Long responsibleId) {
        Long oldResponsibleId = (oldResponsible != null) ? oldResponsible.getId() : null;
        return !Objects.equals(oldResponsibleId, responsibleId);
    }

    private void updateAuthorities(Employee employee, EmployeeDTO employeeDTO) {
        Set<UserAuthorities> authorities = employee.getAuthorities();
        String roleString = ObjectUtils.defaultIfNull(employeeDTO.getRole(), "user");
        UserAuthorities newRole = UserAuthorities.fromString(roleString);
        if (roleChanged(employee, newRole)) {
            changeRole(authorities, newRole);
        }

        if (employeeDTO.isCanCreatePersonalLocation()) {
            authorities.add(UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        } else {
            authorities.remove(UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }

        tokenService.updateTokens(employee.getId(), authorities);
    }

    private boolean roleChanged(Employee employee, UserAuthorities newRole) {
        return !employee.getRole().equals(newRole);
    }

    private void changeRole(Set<UserAuthorities> authorities, UserAuthorities role) {
        authorities.remove(UserAuthorities.USER);
        authorities.remove(UserAuthorities.ADMIN);
        authorities.add(role);
    }

    @Override
    public void changePassword(@NonNull String username, @NonNull String newPassword) {
        employeeRepository.changePassword(username, newPassword);
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long employeeId, @NonNull ArchivedStatusDTO archivedStatusDTO) {
        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));

        employee.setArchived(archivedStatusDTO.isArchived());
        employee.setArchiveReason(archivedStatusDTO.getArchiveReason());
        employeeRepository.save(employee);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void grantAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employee.getAuthorities().add(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void revokeAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employee.getAuthorities().remove(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Transactional
    @Override
    public void performReview(Long employeeId, ReviewDTO reviewDTO) {
        reviewDtoBaseValidator.validate(reviewDTO);

        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        EmployeeReview employeeReview = mapEmployeeReview(reviewDTO, employee);

        ReviewTemplate reviewTemplate = employee.getDetails().getReviewTemplate();
        if (reviewTemplate == null) {
            throw new ReviewNotApplicableException(EMPLOYEE_REVIEW_NOT_APPLICABLE, employee.getId());
        }
        employeeReview.setReviewTemplate(reviewTemplate);

        employeeReviewRepository.save(employeeReview);
    }

    private EmployeeReview mapEmployeeReview(ReviewDTO reviewDTO, Employee subject) {
        reviewDtoBaseValidator.validate(reviewDTO);

        EmployeeReview employeeReview = new EmployeeReview();
        employeeReview.setSubject(subject);
        employeeReview.setReviewer(subject.getDetails().getResponsibleOfSkills());

        List<EmployeeReviewQuestionAnswer> questionAnswers = new ArrayList<>();
        for (ReviewQuestionAnswerDTO answer : reviewDTO.getAnswers()) {
            ReviewQuestion reviewQuestion = reviewQuestionService.getById(answer.getQuestionId())
                    .orElseThrow(() -> new NotFoundException(REVIEW_QUESTION_ID_NOT_FOUND, answer.getQuestionId().toString()));
            EmployeeReviewQuestionAnswer questionAnswer = new EmployeeReviewQuestionAnswer();
            questionAnswer.setReviewQuestion(reviewQuestion);
            questionAnswer.setReview(employeeReview);
            questionAnswer.setAnswer(answer.getAnswer());
            questionAnswer.setComment(answer.getComment());
            questionAnswers.add(questionAnswer);
        }
        employeeReview.setAnswers(questionAnswers);
        return employeeReview;
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Override
    public List<Employee> getAllForReview() {
        return employeeRepository.findAllForReview();
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Long userId, @NonNull Pageable pageable) {
        return getNewEmployees(userId, pageable, null);
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Long userId, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = getById(userId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, userId.toString()));
        Long customerId = employee.getCustomer().getId();

        Predicate newEmployeesPredicate = ExpressionUtils.allOf(
                predicate,
                QEmployee.employee.archived.eq(Boolean.FALSE),
                ExpressionUtils.neConst(QEmployee.employee.id, userId),
                ExpressionUtils.notIn(Expressions.constant(employee), QEmployee.employee.visitedBy));
        return getAllByCustomer(customerId, newEmployeesPredicate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetEmployeeDTO> getEmployeeDtoById(Long employeeId) {
        return getById(employeeId).map(this::mapModelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeePreviewDTO> getPreviewDtoById(Long employeeId) {
        return getById(employeeId).map(this::mapModelToPreviewDto);
    }

    @Override
    public Employee getCurrentUserReference() {
        Long currentUserId = getCurrentUser().getUserId();
        return employeeRepository.getOne(currentUserId);
    }

    private void addAuthorities(Employee employee, UserAuthorities userAuthorities) {
        employeeRepository.addAuthorities(employee.getId(), userAuthorities);
    }

    private Employee resolveResponsibleOfSkills(Long responsibleId) {
        return getById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_OF_SKILL_NOT_FOUND, responsibleId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return reviewTemplateService.getById(reviewTemplateId)
                .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private Set<Location> resolveLocations(Set<Long> locationIds) {
        return new HashSet<>(locationService.getAllById(locationIds));
    }

    private void sendMessages(EmployeeCreateDTO employee, Employee responsible) {
        if (employee.isWelcomeMessage()) {
            sendWelcomeMessage(employee);
        }

        if (responsible != null) {
            sendNotificationToResponsible(responsible);
        }
    }

    private void sendWelcomeMessage(EmployeeCreateDTO employee) {
        Map<String, Object> model = ImmutableMap.<String, Object>builder()
                .put("username", employee.getUsername())
                .put("password", employee.getPassword())
                .build();
        EmailNotification welcomeMessage = new EmailNotification(employee.getUsername(), "welcome", model);
        emailNotificationRepository.save(welcomeMessage);
    }

    private void sendNotificationToResponsible(Employee responsible) {
        EmailNotification notification =
                new EmailNotification(responsible.getUsername(), "employee_created");
        emailNotificationRepository.save(notification);
    }

    //todo It'd be better to replace this with appropriate ModelMapper configuration
    //#see ModelMapper.addMappings()
    private GetEmployeeDTO mapModelToDto(Employee employee) {
        GetEmployeeDTO dto = modelMapper.map(employee, GetEmployeeDTO.class);
        if (employee.getResponsible() != null) {
            dto.setResponsibleId(employee.getResponsible().getId());
        }

        Set<Long> locationIds = employee.getLocations().stream().map(Location::getId).collect(Collectors.toSet());
        dto.setLocationIds(locationIds);

        if (employee.getDetails().getResponsibleOfSkills() != null) {
            dto.getDetails().setSkillResponsibleId(employee.getDetails().getResponsibleOfSkills().getId());
        }

        if (employee.getDetails().getReviewTemplate() != null) {
            dto.getDetails().setReviewTemplateId(employee.getDetails().getReviewTemplate().getId());
        }

        return dto;
    }

    //todo It'd be better to replace this with appropriate ModelMapper configuration
    //#see ModelMapper.addMappings()
    private EmployeePreviewDTO mapModelToPreviewDto(Employee employee) {
        EmployeePreviewDTO dto = modelMapper.map(employee, EmployeePreviewDTO.class);
        if (employee.getResponsible() != null) {
            dto.setResponsibleName(employee.getResponsible().getName());
        }

        Set<String> locationNames = employee.getLocations().stream().map(Location::getName).collect(Collectors.toSet());
        dto.setLocationNames(locationNames);

        if (employee.getDetails().getResponsibleOfSkills() != null) {
            dto.getDetails().setSkillResponsibleName(employee.getDetails().getResponsibleOfSkills().getName());
        }

        if (employee.getDetails().getReviewTemplate() != null) {
            dto.getDetails().setReviewTemplateName(employee.getDetails().getReviewTemplate().getName());
        }

        return dto;
    }

    //region GET/DELETE standard API

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Page<Employee> getAll(@NonNull Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public List<Employee> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(employeeRepository.findAll(predicate));
    }

    @Override
    public Page<Employee> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return employeeRepository.findAll(predicate, pageable);
    }

    @Override
    public List<Employee> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<Employee> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return employeeRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Employee> getById(@NonNull Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> getAllById(@NonNull Iterable<Long> ids) {
        return employeeRepository.findAllById(ids);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteById(@NonNull Long id) {
        tokenService.invalidateByUserId(id);
        employeeRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteByIds(@NonNull Iterable<Long> employeeIds) {
        tokenService.invalidateByUserIds(employeeIds);
        employeeRepository.deleteByIdIn(employeeIds);
    }

    //endregion
}
