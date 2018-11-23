package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.LocationRepository;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.api.JwtTokenService;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.ChangePasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDetailsDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewQuestionAnswerDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeEditDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.EmployeeDetails;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.review.EmployeeReview;
import dk.ledocsystem.data.model.review.EmployeeReviewQuestionAnswer;
import dk.ledocsystem.data.model.review.ReviewQuestion;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.EmployeeReviewRepository;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.ReviewQuestionService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeePreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeSummaryDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import dk.ledocsystem.service.api.exceptions.ReviewNotApplicableException;
import dk.ledocsystem.service.impl.property_maps.employee.EmployeeToEditDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.employee.EmployeeToGetEmployeeDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.employee.EmployeeToPreviewDtoPropertyMap;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final ReviewQuestionService reviewQuestionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final EmailNotificationRepository emailNotificationRepository;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final ModelMapper modelMapper;
    private final BaseValidator<EmployeeDTO> employeeDtoValidator;
    private final BaseValidator<EmployeeCreateDTO> employeeCreateDtoValidator;
    private final BaseValidator<ReviewDTO> reviewDtoBaseValidator;
    private final BaseValidator<ChangePasswordDTO> changePasswordDtoValidator;
    private final EmployeeProducer employeeProducer;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new EmployeeToGetEmployeeDtoPropertyMap());
        modelMapper.addMappings(new EmployeeToEditDtoPropertyMap());
        modelMapper.addMappings(new EmployeeToPreviewDtoPropertyMap());
    }

    @Transactional
    @Override
    public GetEmployeeDTO createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull UserDetails creatorDetails) {
        Long customerId = employeeRepository.findByUsername(creatorDetails.getUsername())
                .map(employee -> employee.getCustomer().getId())
                .orElseThrow(IllegalStateException::new);
        return createEmployee(employeeCreateDTO, customerId, creatorDetails);
    }

    @Transactional
    @Override
    public GetEmployeeDTO createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, Long customerId,
                                         @NonNull UserDetails creatorDetails) {
        employeeCreateDtoValidator.validate(employeeCreateDTO);

        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(resolveCustomer(customerId));

        Employee responsible = resolveResponsible(employeeCreateDTO.getResponsibleId());
        employee.setResponsible(responsible);

        Set<Location> locations = resolveLocations(employeeCreateDTO.getLocationIds());
        employee.setLocations(locations);

        if (employeeDetailsPresent(employeeCreateDTO)) {
            updateReviewDetails(employeeCreateDTO.getDetails(), employee.getDetails());
        }

        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, creatorDetails.getUsername()));
        employee.setCreator(creator);

        employee = employeeRepository.save(employee);

        addAuthorities(employee, employeeCreateDTO);
        sendMessages(employeeCreateDTO, responsible);

        employeeProducer.create(employee, currentUser);
        return mapToDto(employee);
    }

    private void addAuthorities(Employee employee, EmployeeCreateDTO employeeCreateDTO) {
        Set<UserAuthorities> authorities = employee.getAuthorities();
        String roleString = ObjectUtils.defaultIfNull(employeeCreateDTO.getRole(), "user");
        authorities.add(UserAuthorities.fromString(roleString));

        if (employeeCreateDTO.isCanCreatePersonalLocation()) {
            authorities.add(UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }
    }

    @Transactional
    @Override
    public GetEmployeeDTO createPointOfContact(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull UserDetails creator) {
        GetEmployeeDTO pointOfContact = createEmployee(employeeCreateDTO, null, creator);
        employeeRepository.addAuthorities(pointOfContact.getId(), UserAuthorities.SUPER_ADMIN);
        return pointOfContact;
    }

    @Transactional
    @Override
    public GetEmployeeDTO updateEmployee(@NonNull EmployeeDTO employeeDTO, @NonNull UserDetails currentUserDetails) {
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

        employeeProducer.edit(employee, currentUser);

        return mapToDto(employeeRepository.save(employee));
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
    public void changeArchivedStatus(@NonNull Long employeeId, @NonNull ArchivedStatusDTO archivedStatusDTO,
                                     @NonNull UserDetails currentUserDetails) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));

        employee.setArchived(archivedStatusDTO.isArchived());
        employee.setArchiveReason(archivedStatusDTO.getArchiveReason());

        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        if (archivedStatusDTO.isArchived()) {
            employeeProducer.archive(employee, currentUser);
        } else {
            employeeProducer.unarchive(employee, currentUser);
        }

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void changePassword(@NonNull Long employeeId, @NonNull ChangePasswordDTO changePasswordDTO) {
        changePasswordDtoValidator.validate(changePasswordDTO);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));

        employee.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        employeeRepository.save(employee);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void grantAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employee.getAuthorities().add(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void revokeAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employee.getAuthorities().remove(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Transactional
    @Override
    public void performReview(@NonNull Long employeeId, @NonNull ReviewDTO reviewDTO, @NonNull UserDetails currentUserDetails) {
        reviewDtoBaseValidator.validate(reviewDTO);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        EmployeeReview employeeReview = mapEmployeeReview(reviewDTO, employee);

        ReviewTemplate reviewTemplate = employee.getDetails().getReviewTemplate();
        if (reviewTemplate == null) {
            throw new ReviewNotApplicableException(EMPLOYEE_REVIEW_NOT_APPLICABLE, employee.getId());
        }
        employeeReview.setReviewTemplate(reviewTemplate);

        employeeReviewRepository.save(employeeReview);

        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        employeeProducer.review(employee, currentUser);
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

    @Transactional(readOnly = true)
    @Override
    public Optional<GetEmployeeDTO> getByUsername(@NonNull String username) {
        return employeeRepository.findByUsername(username).map(this::mapToDto);
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getNewEmployees(@NonNull UserDetails user, @NonNull Pageable pageable) {
        return getNewEmployees(user, pageable, null);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getNewEmployees(@NonNull UserDetails user, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));
        Long customerId = employee.getCustomer().getId();

        Predicate newEmployeesPredicate = ExpressionUtils.allOf(
                predicate,
                QEmployee.employee.archived.eq(Boolean.FALSE),
                ExpressionUtils.neConst(QEmployee.employee.id, employee.getId()),
                ExpressionUtils.notIn(Expressions.constant(employee), QEmployee.employee.visitedBy));
        return getAllByCustomer(customerId, newEmployeesPredicate, pageable);
    }

    @Override
    @Transactional
    public Optional<EmployeePreviewDTO> getPreviewDtoById(@NonNull Long employeeId, boolean isSaveLog,
                                                          @NonNull UserDetails currentUserDetails) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);

        if (isSaveLog) {
            employee.ifPresent(empl -> {
                Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
                employeeLogService.createLog(currentUser, empl, LogType.Read);
            });
        }
        rreturn employee.map(this::mapToPreviewDto);
    }

    private Customer resolveCustomer(Long customerId) {
        return (customerId == null) ? null :
                customerRepository.findById(customerId)
                        .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerId.toString()));
    }

    private Employee resolveResponsibleOfSkills(Long responsibleId) {
        return employeeRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_OF_SKILL_NOT_FOUND, responsibleId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return reviewTemplateService.getById(reviewTemplateId)
                .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                employeeRepository.findById(responsibleId)
                        .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private Set<Location> resolveLocations(Set<Long> locationIds) {
        return new HashSet<>(locationRepository.findAllById(locationIds));
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

    //region GET/DELETE standard API

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAll(@NonNull Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return employeeRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return employeeRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    public List<EmployeeSummaryDTO> getAllNamesByCustomer(Long customerId) {
        return employeeRepository.findAllBy(customerId)
                .stream()
                .map(EmployeeSummaryDTO::new)
                .collect(Collectors.groupingBy(EmployeeSummaryDTO::getId, Collectors.reducing((first, second) -> {
                    first.getLocations().addAll(second.getLocations()); // ugly workaround to overcome Spring Data's inability
                    return first;                                       // to group results with custom @Query
                })))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<GetEmployeeDTO> getById(@NonNull Long id) {
        return employeeRepository.findById(id).map(this::mapToEditDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAllById(@NonNull Iterable<Long> ids) {
        return employeeRepository.findAllById(ids).stream().map(this::mapToEditDto).collect(Collectors.toList());
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

    private GetEmployeeDTO mapToDto(Employee employee) {
        return modelMapper.map(employee, GetEmployeeDTO.class);
    }

    private EmployeeEditDTO mapToEditDto(Employee employee) {
        return modelMapper.map(employee, EmployeeEditDTO.class);
    }

    private EmployeePreviewDTO mapToPreviewDto(Employee employee) {
        return modelMapper.map(employee, EmployeePreviewDTO.class);
    }

    //endregion
}
