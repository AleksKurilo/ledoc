package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.EmployeeDetails;
import dk.ledocsystem.data.model.employee.FollowedEmployees;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.review.*;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.EmployeeReviewRepository;
import dk.ledocsystem.data.repository.LocationRepository;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.api.ReviewQuestionService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.ChangePasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDetailsDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeFollowDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewQuestionAnswerDTO;
import dk.ledocsystem.service.api.dto.inbound.review.SimpleReviewDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.*;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.api.exceptions.ReviewNotApplicableException;
import dk.ledocsystem.service.impl.events.producer.EmployeeProducer;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.employees.EmployeesEntitySheet;
import dk.ledocsystem.service.impl.property_maps.employee.*;
import dk.ledocsystem.service.impl.utils.PredicateBuilderAndParser;
import dk.ledocsystem.service.impl.utils.QueryHandler;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);
    private static final Function<Boolean, Predicate> EMPLOYEES_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QEmployee.employee.archived, archived);

    @PersistenceContext
    private EntityManager entityManager;

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final ReviewQuestionService reviewQuestionService;
    private final ExcelExportService excelExportService;
    private final PasswordEncoder passwordEncoder;
    private final EntityManagerFactory entityManagerFactory;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final EmployeeProducer employeeProducer;
    private final ModelMapper modelMapper;
    private final QueryHandler queryHandler;

    private final BaseValidator<EmployeeDTO> employeeDtoValidator;
    private final BaseValidator<EmployeeCreateDTO> employeeCreateDtoValidator;
    private final BaseValidator<ReviewDTO> reviewDtoValidator;
    private final BaseValidator<SimpleReviewDTO> simpleReviewDtoValidator;
    private final BaseValidator<ChangePasswordDTO> changePasswordDtoValidator;
    private final PredicateBuilderAndParser predicateBuilderAndParser;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new EmployeeToGetEmployeeDtoPropertyMap());
        modelMapper.addMappings(new EmployeeToEditDtoPropertyMap());
        modelMapper.addMappings(new EmployeeToPreviewDtoPropertyMap());
        modelMapper.addMappings(new FollowedEmployeesToGetFollowedEmployeesDtoMap());
        modelMapper.addMappings(new EmployeeToExportDtoMap());
        modelMapper.addMappings(new EmployeeToSummaryPropertyMap());
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
        employeeCreateDtoValidator.validate(employeeCreateDTO, employeeCreateDTO.getValidationGroups());

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

        employeeProducer.create(employeeCreateDTO, creator);
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
        employeeDtoValidator.validate(employeeDTO, employeeDTO.getValidationGroups());

        Employee employee = employeeRepository.findById(employeeDTO.getId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeDTO.getId().toString()));

        modelMapper.map(employeeDTO, employee);

        Long responsibleId = employeeDTO.getResponsibleId();
        if (responsibleChanged(employee.getResponsible(), responsibleId)) {
            Employee responsible = resolveResponsible(responsibleId);
            employee.setResponsible(responsible);
        }

        if (locationsChanged(employee, employeeDTO.getLocationIds())) {
            employee.setLocations(resolveLocations(employeeDTO.getLocationIds()));
        }

        if (employeeDetailsPresent(employeeDTO)) {
            updateReviewDetails(employeeDTO.getDetails(), employee.getDetails());
        }

        updateAuthorities(employee, employeeDTO);

        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
            Employee employeeBeforeEdit = session.get(Employee.class, employee.getId());
            employeeProducer.edit(employeeBeforeEdit, employee, currentUser);
        }

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

    private boolean locationsChanged(Employee employee, Set<Long> locationIds) {
        return !employee.getLocations()
                .stream()
                .map(Location::getId)
                .collect(Collectors.toSet())
                .equals(locationIds);
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
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void revokeAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employee.getAuthorities().remove(authorities);
    }

    @Transactional
    @Override
    public void performSimpleReview(@NonNull Long employeeId, @NonNull SimpleReviewDTO reviewDTO, @NonNull UserDetails currentUserDetails) {
        simpleReviewDtoValidator.validate(reviewDTO);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);

        ReviewTemplate reviewTemplate = employee.getDetails().getReviewTemplate();
        if (reviewTemplate == null || !reviewTemplate.isSimple()) {
            throw new ReviewNotApplicableException(EMPLOYEE_SIMPLE_REVIEW_NOT_APPLICABLE, employee.getId());
        }

        EmployeeReview employeeReview = mapSimpleEmployeeReview(reviewDTO, reviewTemplate);
        employeeReview.setReviewTemplate(reviewTemplate);
        employeeReview.setSubject(employee);
        employeeReview.setReviewer(currentUser);

        employeeReviewRepository.save(employeeReview);
        employeeProducer.review(employee, currentUser);
    }

    private EmployeeReview mapSimpleEmployeeReview(SimpleReviewDTO reviewDTO, ReviewTemplate reviewTemplate) {
        EmployeeReview employeeReview = new EmployeeReview();

        EmployeeReviewQuestionAnswer questionAnswer = new EmployeeReviewQuestionAnswer();
        questionAnswer.setReviewQuestion(reviewTemplate.getQuestionGroups().get(0).getReviewQuestions().get(0));
        questionAnswer.setReview(employeeReview);
        questionAnswer.setAnswer(reviewDTO.getStatus().toString());

        employeeReview.setAnswers(Collections.singletonList(questionAnswer));
        return employeeReview;
    }

    @Transactional
    @Override
    public void performReview(@NonNull Long employeeId, @NonNull ReviewDTO reviewDTO, @NonNull UserDetails currentUserDetails) {
        reviewDtoValidator.validate(reviewDTO);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);

        ReviewTemplate reviewTemplate = employee.getDetails().getReviewTemplate();
        if (reviewTemplate == null) {
            throw new ReviewNotApplicableException(EMPLOYEE_REVIEW_NOT_APPLICABLE, employee.getId());
        }

        EmployeeReview employeeReview = mapEmployeeReview(reviewDTO);
        employeeReview.setReviewTemplate(reviewTemplate);
        employeeReview.setSubject(employee);
        employeeReview.setReviewer(currentUser);

        employeeReviewRepository.save(employeeReview);
        employeeProducer.review(employee, currentUser);
    }

    private EmployeeReview mapEmployeeReview(ReviewDTO reviewDTO) {
        EmployeeReview employeeReview = new EmployeeReview();

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
    public long countNewEmployees(@NonNull UserDetails user) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));

        Predicate newEmployeesPredicate = ExpressionUtils.allOf(CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()), getNewEmployeesPredicate(employee));
        return employeeRepository.count(newEmployeesPredicate);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getNewEmployees(@NonNull UserDetails user, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));

        Predicate combinePredicate = ExpressionUtils.allOf(predicate, CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()), getNewEmployeesPredicate(employee));
        return getAll(combinePredicate, pageable);
    }

    private Predicate getNewEmployeesPredicate(Employee employee) {
        return ExpressionUtils.allOf(
                EMPLOYEES_ARCHIVED.apply(Boolean.FALSE),
                ExpressionUtils.neConst(QEmployee.employee.id, employee.getId()),
                ExpressionUtils.eqConst(QEmployee.employee.visitedLogs.any().employee, employee).not());
    }

    @Override
    @Transactional
    public Optional<EmployeePreviewDTO> getPreviewDtoById(@NonNull Long employeeId, boolean isSaveLog,
                                                          @NonNull UserDetails currentUserDetails) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);

        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        employee.ifPresent(empl -> employeeProducer.read(empl, currentUser, isSaveLog));
        return employee.map(this::mapToPreviewDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Workbook exportToExcel(UserDetails currentUserDetails, String searchString, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> employeesSheets = new ArrayList<>();
        Predicate predicateForEmployees = ExpressionUtils.and(predicate, EMPLOYEES_ARCHIVED.apply(false));
        employeesSheets.add(new EmployeesEntitySheet(this, currentUserDetails, searchString, predicateForEmployees, isNew, false, "Employees"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, EMPLOYEES_ARCHIVED.apply(true));
            employeesSheets.add(new EmployeesEntitySheet(this, currentUserDetails, searchString, predicateForArchived, false, true, "Archived"));
        }
        return excelExportService.exportSheets(employeesSheets);
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
    public List<GetEmployeeDTO> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetEmployeeDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false, false);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetEmployeeDTO> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew, boolean isArchived) {
        predicate = ExpressionUtils.allOf(predicate, EMPLOYEES_ARCHIVED.apply(isArchived));
        JPAQuery query = getAllByCustomerForPreviewAndExport(currentUser, searchString, predicate, isNew, isArchived);

        long count = query.fetchCount();

        queryHandler.sortPageableQuery(query, pageable, QEmployee.employee);

        Page<Employee> result = new PageImpl<>(query.fetch(), pageable, count);
        return result.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeSummary> getAllNamesByCustomer(Long customerId) {
        Predicate combinePredicate = ExpressionUtils.and(EMPLOYEES_ARCHIVED.apply(Boolean.FALSE), CUSTOMER_EQUALS_TO.apply(customerId));
        return employeeRepository.findAll(combinePredicate).stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeExportDTO> getAllForExport(UserDetails creatorDetails, String searchString, Predicate predicate, boolean isNew, boolean isArchived) {
        QEmployee qEmployee = QEmployee.employee;

        JPAQuery query = getAllByCustomerForPreviewAndExport(creatorDetails, searchString, predicate, isNew, isArchived);
        query.orderBy(qEmployee.lastName.asc());

        List<Employee> resultList = query.fetch();
        return resultList.stream().map(this::mapToExportDto).collect(Collectors.toList());
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
        employeeRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteByIds(@NonNull Iterable<Long> employeeIds) {
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

    private GetFollowedEmployeeDTO mapToFollowDto(FollowedEmployees employee) {
        return modelMapper.map(employee, GetFollowedEmployeeDTO.class);
    }

    private EmployeeExportDTO mapToExportDto(Employee employee) {
        return modelMapper.map(employee, EmployeeExportDTO.class);
    }

    private EmployeeSummary mapToSummary(Employee employee) {
        return modelMapper.map(employee, EmployeeSummary.class);
    }

    @Override
    @Transactional
    public void follow(Long employeeId, UserDetails currentUserDetails, EmployeeFollowDTO employeeFollowDTO) {
        Employee followedEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, employeeFollowDTO.toString()));

        Employee follower;
        boolean forced = false;
        if (employeeFollowDTO.getFollowerId() != null) {
            follower = employeeRepository.findById(employeeFollowDTO.getFollowerId()).orElseThrow(IllegalStateException::new);
            forced = true;
        } else {
            follower = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        }
        if (employeeFollowDTO.isFollowed()) {
            followedEmployee.addFollower(follower, forced);
        } else {
            followedEmployee.removeFollower(follower);
        }
        employeeProducer.follow(followedEmployee, follower, forced, employeeFollowDTO.isFollowed());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetFollowedEmployeeDTO> getFollowedEmployees(Long employeeId, Pageable pageable) {
        return employeeRepository.findAllFollowedEmployeesByEmployeePaged(employeeId, pageable).map(this::mapToFollowDto);
    }

    JPAQuery getAllByCustomerForPreviewAndExport(UserDetails currentUser, String searchString, Predicate predicate, boolean isNew, boolean isArchived) {
        QEmployee qEmployee = QEmployee.employee;

        QEmployee qResponsible = new QEmployee("responsible");
        QEmployee qResponsibleOfSkills = new QEmployee("skills_responsible");

        JPAQuery query = new JPAQuery<>(entityManager);
        query.from(qEmployee).distinct();

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(searchString)) {
            predicates = Stream.of(
                    Pair.of(qEmployee.firstName, searchString),
                    Pair.of(qEmployee.lastName, searchString),
                    Pair.of(qEmployee.username, searchString),
                    Pair.of(qEmployee.authorities.any(), searchString),
                    Pair.of(qEmployee.locations.any().name, searchString),
                    Pair.of(qEmployee.responsible.firstName, searchString),
                    Pair.of(qEmployee.responsible.lastName, searchString),
                    Pair.of(qEmployee.title, searchString),
                    Pair.of(qEmployee.initials, searchString),
                    Pair.of(qEmployee.phoneNumber, searchString),
                    Pair.of(qEmployee.cellPhone, searchString),
                    Pair.of(qEmployee.idNumber, searchString),
                    Pair.of(qEmployee.personalInfo.address, searchString),
                    Pair.of(qEmployee.personalInfo.buildingNo, searchString),
                    Pair.of(qEmployee.personalInfo.postalCode, searchString),
                    Pair.of(qEmployee.personalInfo.city, searchString),
                    Pair.of(qEmployee.personalInfo.personalMobile, searchString),
                    Pair.of(qEmployee.personalInfo.privateEmail, searchString),
                    Pair.of(qEmployee.personalInfo.comment, searchString),
                    Pair.of(qEmployee.details.responsibleOfSkills.firstName, searchString),
                    Pair.of(qEmployee.details.responsibleOfSkills.lastName, searchString),
                    Pair.of(qEmployee.details.reviewTemplate.name, searchString),
                    Pair.of(qEmployee.details.comment, searchString),
                    Pair.of(qEmployee.nearestRelative.firstName, searchString),
                    Pair.of(qEmployee.nearestRelative.lastName, searchString),
                    Pair.of(qEmployee.nearestRelative.phoneNumber, searchString),
                    Pair.of(qEmployee.nearestRelative.email, searchString),
                    Pair.of(qEmployee.nearestRelative.comment, searchString)
            ).map(predicateBuilderAndParser::toPredicate)
                    .collect(Collectors.toList());

            query.leftJoin(qEmployee.responsible, qResponsible)
                    .leftJoin(qEmployee.details.responsibleOfSkills, qResponsibleOfSkills)
                    .leftJoin(qEmployee.details.reviewTemplate, QReviewTemplate.reviewTemplate);
        }

        Employee employee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);

        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()));
        if (!isArchived && isNew) {
            combinePredicate = ExpressionUtils.allOf(combinePredicate,
                    getNewEmployeesPredicate(employee));
        }

        combinePredicate = ExpressionUtils.and(combinePredicate, ExpressionUtils.anyOf(predicates));

        query.where(combinePredicate);

        return query;
    }
    //endregion
}
