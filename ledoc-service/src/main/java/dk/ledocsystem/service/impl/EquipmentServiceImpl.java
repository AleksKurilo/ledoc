package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.QLocation;
import dk.ledocsystem.data.model.QSupplier;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.equipment.*;
import dk.ledocsystem.data.model.review.EquipmentReview;
import dk.ledocsystem.data.model.review.EquipmentReviewQuestionAnswer;
import dk.ledocsystem.data.model.review.ReviewQuestion;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.repository.*;
import dk.ledocsystem.service.api.EquipmentService;
import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.api.ReviewQuestionService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.*;
import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentLoanDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewQuestionAnswerDTO;
import dk.ledocsystem.service.api.dto.inbound.review.SimpleReviewDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.equipment.*;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.api.exceptions.ReviewNotApplicableException;
import dk.ledocsystem.service.impl.events.producer.EquipmentProducer;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.equipment.EquipmentEntitySheet;
import dk.ledocsystem.service.impl.property_maps.equipment.*;
import dk.ledocsystem.service.impl.utils.PredicateBuilderAndParser;
import dk.ledocsystem.service.impl.utils.QueryHandler;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEquipment.equipment.customer.id, customerId);
    private static final Function<Boolean, Predicate> EQUIPMENT_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QEquipment.equipment.archived, archived);

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;
    private final ExcelExportService excelExportService;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final AuthenticationTypeRepository authenticationTypeRepository;
    private final EquipmentReviewRepository equipmentReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final ReviewQuestionService reviewQuestionService;
    private final EquipmentProducer equipmentProducer;
    private final EntityManagerFactory entityManagerFactory;
    private final PredicateBuilderAndParser predicateBuilderAndParser;
    private final QueryHandler queryHandler;

    private final BaseValidator<EquipmentDTO> equipmentDtoValidator;
    private final BaseValidator<EquipmentLoanDTO> equipmentLoanDtoValidator;
    private final BaseValidator<AuthenticationTypeDTO> authenticationTypeDtoValidator;
    private final BaseValidator<EquipmentCategoryCreateDTO> equipmentCategoryCreateDtoValidator;
    private final BaseValidator<ReviewDTO> reviewDtoValidator;
    private final BaseValidator<SimpleReviewDTO> simpleReviewDtoValidator;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new EquipmentToGetEquipmentDtoPropertyMap());
        modelMapper.addMappings(new EquipmentToEditDtoPropertyMap());
        modelMapper.addMappings(new EquipmentToPreviewDtoPropertyMap());
        modelMapper.addMappings(new FollowedEquipmentToGetFollowedEquipmentDtoMap());
        modelMapper.addMappings(new EquipmentToExportDtoMap());
    }

    @Override
    @Transactional
    public GetEquipmentDTO createEquipment(@NonNull EquipmentDTO equipmentDTO, @NonNull UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Customer customer = creator.getCustomer();
        equipmentDtoValidator.validate(equipmentDTO, ImmutableMap.of("customerId", customer.getId()), equipmentDTO.getValidationGroups());

        Equipment equipment = modelMapper.map(equipmentDTO, Equipment.class);
        Employee responsible = resolveResponsible(equipmentDTO.getResponsibleId());

        equipment.setCreator(creator);
        equipment.setResponsible(responsible);
        equipment.setCustomer(customer);
        equipment.setCategory(resolveCategory(equipmentDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentDTO.getLocationId()));
        equipment.setReviewTemplate(resolveReviewTemplate(equipmentDTO.getReviewTemplateId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentDTO.getAuthTypeId()));

        equipmentProducer.create(equipment, creator);

        return mapToDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public GetEquipmentDTO updateEquipment(@NonNull EquipmentDTO equipmentDTO, @NonNull UserDetails currentUserDetails) {
        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Customer customer = currentUser.getCustomer();
        equipmentDtoValidator.validate(equipmentDTO, ImmutableMap.of("customerId", customer.getId()), equipmentDTO.getValidationGroups());

        Equipment equipment = equipmentRepository.findById(equipmentDTO.getId())
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentDTO.getId().toString()));
        modelMapper.map(equipmentDTO, equipment);

        equipment.setCategory(resolveCategory(equipmentDTO.getCategoryId()));
        equipment.setLocation(resolveLocation(equipmentDTO.getLocationId()));
        equipment.setReviewTemplate(resolveReviewTemplate(equipmentDTO.getReviewTemplateId()));
        equipment.setAuthenticationType(resolveAuthenticationType(equipmentDTO.getAuthTypeId()));

        Long responsibleId = equipmentDTO.getResponsibleId();
        if (!responsibleId.equals(equipment.getResponsible().getId())) {
            Employee responsible = resolveResponsible(responsibleId);
            equipment.setResponsible(responsible);
        }

        ApprovalType approvalType = equipmentDTO.getApprovalType();
        if (approvalType == ApprovalType.NO_NEED) {
            equipment.eraseReviewDetails();
        }

        try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
            Equipment equipmentBeforeEdit = session.get(Equipment.class, equipment.getId());
            equipmentProducer.edit(equipmentBeforeEdit, equipment, currentUser);
        }
        return mapToDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long equipmentId, @NonNull ArchivedStatusDTO archivedStatusDTO, @NonNull UserDetails creatorDetails) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));

        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);

        equipment.setArchived(archivedStatusDTO.isArchived());
        equipment.setArchiveReason(archivedStatusDTO.getArchiveReason());
        equipmentRepository.save(equipment);
        if (archivedStatusDTO.isArchived()) {
            equipmentProducer.archive(equipment, creator);
        } else {
            equipmentProducer.unarchive(equipment, creator);
        }
    }

    @Transactional
    @Override
    public void performSimpleReview(@NonNull Long equipmentId, @NonNull SimpleReviewDTO reviewDTO, @NonNull UserDetails currentUserDetails) {
        simpleReviewDtoValidator.validate(reviewDTO);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);

        ReviewTemplate reviewTemplate = equipment.getReviewTemplate();
        if (reviewTemplate == null || !reviewTemplate.isSimple()) {
            throw new ReviewNotApplicableException(EQUIPMENT_SIMPLE_REVIEW_NOT_APPLICABLE, equipment.getId());
        }

        EquipmentReview equipmentReview = mapSimpleEquipmentReview(reviewDTO, reviewTemplate);
        equipmentReview.setReviewTemplate(reviewTemplate);
        equipmentReview.setSubject(equipment);
        equipmentReview.setReviewer(currentUser);

        equipment.setStatus(reviewDTO.getStatus());
        equipmentReviewRepository.save(equipmentReview);
        equipmentProducer.review(equipment, currentUser);
    }

    private EquipmentReview mapSimpleEquipmentReview(SimpleReviewDTO reviewDTO, ReviewTemplate reviewTemplate) {
        EquipmentReview equipmentReview = new EquipmentReview();

        EquipmentReviewQuestionAnswer questionAnswer = new EquipmentReviewQuestionAnswer();
        questionAnswer.setReviewQuestion(reviewTemplate.getQuestionGroups().get(0).getReviewQuestions().get(0));
        questionAnswer.setReview(equipmentReview);
        questionAnswer.setAnswer(reviewDTO.getStatus().toString());

        equipmentReview.setAnswers(Collections.singletonList(questionAnswer));
        return equipmentReview;
    }

    @Transactional
    @Override
    public void performReview(@NonNull Long equipmentId, @NonNull ReviewDTO reviewDTO, @NonNull UserDetails currentUserDetails) {
        reviewDtoValidator.validate(reviewDTO);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        Employee currentUser = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);

        ReviewTemplate reviewTemplate = equipment.getReviewTemplate();
        if (reviewTemplate == null) {
            throw new ReviewNotApplicableException(EQUIPMENT_REVIEW_NOT_APPLICABLE, equipment.getId());
        }

        EquipmentReview equipmentReview = mapEquipmentReview(reviewDTO);
        equipmentReview.setReviewTemplate(reviewTemplate);
        equipmentReview.setSubject(equipment);
        equipmentReview.setReviewer(currentUser);

        equipmentReviewRepository.save(equipmentReview);
        equipmentProducer.review(equipment, currentUser);
    }

    private EquipmentReview mapEquipmentReview(ReviewDTO reviewDTO) {
        EquipmentReview equipmentReview = new EquipmentReview();

        List<EquipmentReviewQuestionAnswer> questionAnswers = new ArrayList<>();
        for (ReviewQuestionAnswerDTO answer : reviewDTO.getAnswers()) {
            ReviewQuestion reviewQuestion = reviewQuestionService.getById(answer.getQuestionId())
                    .orElseThrow(() -> new NotFoundException(REVIEW_QUESTION_ID_NOT_FOUND, answer.getQuestionId().toString()));
            EquipmentReviewQuestionAnswer questionAnswer = new EquipmentReviewQuestionAnswer();
            questionAnswer.setReviewQuestion(reviewQuestion);
            questionAnswer.setReview(equipmentReview);
            questionAnswer.setAnswer(answer.getAnswer());
            questionAnswer.setComment(answer.getComment());
            questionAnswers.add(questionAnswer);
        }
        equipmentReview.setAnswers(questionAnswers);
        return equipmentReview;
    }

    @Override
    @Transactional(readOnly = true)
    public long countNewEquipment(@NonNull UserDetails user) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));

        Predicate newEquipmentPredicate = ExpressionUtils.allOf(CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()), getNewEquipmentPredicate(employee));
        return equipmentRepository.count(newEquipmentPredicate);
    }

    private Predicate getNewEquipmentPredicate(Employee employee) {
        return ExpressionUtils.allOf(
                EQUIPMENT_ARCHIVED.apply(Boolean.FALSE),
                ExpressionUtils.eqConst(QEquipment.equipment.visitedLogs.any().employee, employee).not());
    }

    @Override
    @Transactional
    public void loanEquipment(@NonNull Long equipmentId, @NonNull EquipmentLoanDTO equipmentLoanDTO) {
        equipmentLoanDtoValidator.validate(equipmentLoanDTO);

        EquipmentLoan equipmentLoan = modelMapper.map(equipmentLoanDTO, EquipmentLoan.class);
        equipmentLoan.setBorrower(resolveBorrower(equipmentLoanDTO.getBorrowerId()));
        equipmentLoan.setLocation(resolveLocation(equipmentLoanDTO.getLocationId()));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipment.setLoan(equipmentLoan);
    }

    @Override
    @Transactional
    public void returnLoanedEquipment(@NonNull Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipment.removeLoan();
    }

    @Override
    @Transactional(readOnly = true)
    public Workbook exportToExcel(@NonNull UserDetails currentUserDetails, String searchString, Predicate predicate, boolean isNew) {
        List<EntitySheet> equipmentSheets = new ArrayList<>();
        Predicate predicateForEquipment = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(false));
        equipmentSheets.add(new EquipmentEntitySheet(this, currentUserDetails, searchString, predicateForEquipment, isNew, "Equipment"));
        if (isPredicateArchived(predicate)) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(true));
            equipmentSheets.add(new EquipmentEntitySheet(this, currentUserDetails, searchString, predicateForArchived, false, "Archived"));
        }
        return excelExportService.exportSheets(equipmentSheets);
    }

    private EquipmentCategory resolveCategory(Long categoryId) {
        return equipmentCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_CATEGORY_NOT_FOUND, categoryId.toString()));
    }

    private Location resolveLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    private ReviewTemplate resolveReviewTemplate(Long reviewTemplateId) {
        return (reviewTemplateId == null) ? null :
                reviewTemplateService.getById(reviewTemplateId)
                        .orElseThrow(() -> new NotFoundException(REVIEW_TEMPLATE_ID_NOT_FOUND, reviewTemplateId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return employeeRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private AuthenticationType resolveAuthenticationType(Long authTypeId) {
        return (authTypeId == null) ? null :
                authenticationTypeRepository.findById(authTypeId)
                        .orElseThrow(() -> new NotFoundException(EQUIPMENT_AUTHENTICATION_TYPE_NOT_FOUND, authTypeId.toString()));
    }

    private Employee resolveBorrower(Long borrowerId) {
        return employeeRepository.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_BORROWER_NOT_FOUND, borrowerId.toString()));
    }

    @Override
    public List<IdAndLocalizedName> getAuthTypes() {
        return authenticationTypeRepository.findAll().stream().map(this::mapAuthTypeToDto).collect(Collectors.toList());
    }

    @Override
    public IdAndLocalizedName createAuthType(AuthenticationTypeDTO authenticationTypeDTO) {
        authenticationTypeDtoValidator.validate(authenticationTypeDTO);

        AuthenticationType authenticationType = modelMapper.map(authenticationTypeDTO, AuthenticationType.class);
        return mapAuthTypeToDto(authenticationTypeRepository.save(authenticationType));
    }

    @Override
    public List<IdAndLocalizedName> getCategories() {
        return equipmentCategoryRepository.findAll().stream().map(this::mapCategoryToDto).collect(Collectors.toList());
    }

    @Override
    public IdAndLocalizedName createCategory(EquipmentCategoryCreateDTO categoryCreateDTO) {
        equipmentCategoryCreateDtoValidator.validate(categoryCreateDTO);

        EquipmentCategory category = modelMapper.map(categoryCreateDTO, EquipmentCategory.class);
        return mapCategoryToDto(equipmentCategoryRepository.save(category));
    }

    //region GET/DELETE standard API

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAll(@NonNull Pageable pageable) {
        return equipmentRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return equipmentRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetEquipmentDTO> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew) {
        JPAQuery query = getAllByCustomerForPreviewAndExport(currentUser, searchString, predicate, isNew);

        long count = query.fetchCount();

        queryHandler.sortPageableQuery(query, pageable, QEquipment.equipment);

        Page<Equipment> result = new PageImpl<>(query.fetch(), pageable, count);
        return result.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentExportDTO> getAllForExport(UserDetails creatorDetails, String searchString, Predicate predicate, boolean isNew) {
        QEquipment equipment = QEquipment.equipment;

        JPAQuery query = getAllByCustomerForPreviewAndExport(creatorDetails, searchString, predicate, isNew);
        query.orderBy(equipment.name.asc());

        List<Equipment> resultList = query.fetch();
        return resultList.stream().map(this::mapToExportDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentPreviewDTO> getPreviewDtoById(@NonNull Long equipmentId, boolean isSaveLog, UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipmentProducer.read(equipment, creator, isSaveLog);
        return equipmentRepository.findById(equipmentId).map(this::mapToPreviewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetEquipmentDTO> getById(@NonNull Long id) {
        return equipmentRepository.findById(id).map(this::mapToEditDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetEquipmentDTO> getAllById(@NonNull Iterable<Long> ids) {
        return equipmentRepository.findAllById(ids).stream().map(this::mapToEditDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(@NonNull Long id) {
        equipmentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> equipmentIds) {
        equipmentRepository.deleteByIdIn(equipmentIds);
    }

    private GetEquipmentDTO mapToDto(Equipment equipment) {
        return modelMapper.map(equipment, GetEquipmentDTO.class);
    }

    private EquipmentEditDto mapToEditDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentEditDto.class);
    }

    private EquipmentPreviewDTO mapToPreviewDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentPreviewDTO.class);
    }

    private GetFollowedEquipmentDTO mapToFollowDto(FollowedEquipment equipment) {
        return modelMapper.map(equipment, GetFollowedEquipmentDTO.class);
    }

    private EquipmentExportDTO mapToExportDto(Equipment equipment) {
        return modelMapper.map(equipment, EquipmentExportDTO.class);
    }

    private IdAndLocalizedName mapAuthTypeToDto(AuthenticationType authenticationType) {
        return modelMapper.map(authenticationType, IdAndLocalizedName.class);
    }

    private IdAndLocalizedName mapCategoryToDto(EquipmentCategory category) {
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetFollowedEquipmentDTO> getFollowedEquipment(Long employeeId, Pageable pageable) {
        return employeeRepository.findAllFollowedEquipmentByEmployeePaged(employeeId, pageable).map(this::mapToFollowDto);
    }

    @Override
    @Transactional
    public void follow(Long equipmentId, UserDetails currentUserDetails, EquipmentFollowDTO equipmentFollowDTO) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));

        Employee follower;
        boolean forced = false;
        if (equipmentFollowDTO.getFollowerId() != null) {
            follower = employeeRepository.findById(equipmentFollowDTO.getFollowerId()).orElseThrow(IllegalStateException::new);
            forced = true;
        } else {
            follower = employeeRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow(IllegalStateException::new);
        }
        if (equipmentFollowDTO.isFollowed()) {
            equipment.addFollower(follower, forced);
        } else {
            equipment.removeFollower(follower);
        }
        equipmentProducer.follow(equipment, follower, forced, equipmentFollowDTO.isFollowed());
    }

    JPAQuery getAllByCustomerForPreviewAndExport(UserDetails currentUser, String searchString, Predicate predicate, boolean isNew) {
        QEquipment equipment = QEquipment.equipment;

        JPAQuery query = new JPAQuery<>(entityManager);
        query.from(equipment);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(searchString)) {
            predicates = Stream.of(
                    Pair.of(equipment.name, searchString),
                    Pair.of(equipment.idNumber, searchString),
                    Pair.of(equipment.serialNumber, searchString),
                    Pair.of(equipment.category.nameEn, searchString),
                    Pair.of(equipment.localId, searchString),
                    Pair.of(equipment.location.name, searchString),
                    Pair.of(equipment.responsible.firstName, searchString),
                    Pair.of(equipment.responsible.lastName, searchString),
                    Pair.of(equipment.manufacturer, searchString),
                    Pair.of(equipment.authenticationType.nameEn, searchString),
                    Pair.of(equipment.supplier.name, searchString),
                    Pair.of(equipment.comment, searchString),
                    Pair.of(equipment.price, searchString)
            ).map(predicateBuilderAndParser::toPredicate)
                    .collect(Collectors.toList());

            query.leftJoin(equipment.supplier, QSupplier.supplier)
                    .leftJoin(equipment.authenticationType, QAuthenticationType.authenticationType)
                    .leftJoin(equipment.category ,QEquipmentCategory.equipmentCategory)
                    .leftJoin(equipment.location ,QLocation.location)
                    .leftJoin(equipment.responsible ,QEmployee.employee);
        }

        Employee employee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);

        Predicate combinePredicate = ExpressionUtils.and(ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId())), ExpressionUtils.anyOf(predicates));


        if (isPredicateArchived(predicate) && isNew) {
            combinePredicate = ExpressionUtils.allOf(combinePredicate,
                    getNewEquipmentPredicate(employee));
        }

        query.where(combinePredicate);

        return query;
    }

    boolean isPredicateArchived(Predicate predicate) {

        QEquipment equipment = QEquipment.equipment;
        List<Expression<?>> argsList = predicateBuilderAndParser.getArgs(predicate);
        if (argsList.size() > 0) {
            return (argsList.get(argsList.indexOf(equipment.archived) + 1).toString() != "true");
        }
        return false;
    }

    //endregion
}
