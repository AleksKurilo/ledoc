package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import dk.ledocsystem.data.model.*;
import dk.ledocsystem.data.model.document.*;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.repository.*;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.api.ReviewTemplateService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentFollowDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentReadStatusDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.document.*;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.events.producer.DocumentProducer;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.documents.DocumentsEntitySheet;
import dk.ledocsystem.service.impl.property_maps.document.*;
import dk.ledocsystem.service.impl.utils.PredicateBuilderAndParser;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QDocument.document.customer.id, customerId);
    private static final Function<Boolean, Predicate> DOCUMENTS_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QDocument.document.archived, archived);

    @PersistenceContext
    private EntityManager entityManager;

    private final TradeRepository tradeRepository;
    private final DocumentProducer documentProducer;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final CustomerService customerService;
    private final DocumentCategoryRepository categoryRepository;
    private final FollowedDocumentRepository followedDocumentRepository;

    private final ExcelExportService excelExportService;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;
    private final BaseValidator<DocumentCategoryDTO> categoryDtoValidator;
    private final PredicateBuilderAndParser predicateBuilderAndParser;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new DocumentToGetDocumentDtoPropertyMap());
        modelMapper.addMappings(new DocumentToEditDtoPropertyMap());
        modelMapper.addMappings(new DocumentToDocumentPreviewDtoPropertyMap());
        modelMapper.addMappings(new DocumentToExportDtoMap());
        modelMapper.addMappings(new FollowedDocumentToGetFollowedDocumentDtoMap());
        modelMapper.addMappings(new FollowedDocumentToEmployeeByDocumentReadStatusDtoMap());
    }

    @Override
    @Transactional
    public GetDocumentDTO create(@NonNull DocumentDTO documentDTO, @NonNull UserDetails userDetails) {
        Document document = modelMapper.map(documentDTO, Document.class);
        Customer customer = resolveCustomerByUsername(userDetails.getUsername());
        documentDtoValidator.validate(documentDTO, ImmutableMap.of("customerId", customer.getId()), documentDTO.getValidationGroups());

        document.setCustomer(customer);
        Employee creator = employeeRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, userDetails.getUsername()));

        document.setCreator(creator);
        document.setResponsible(resolveResponsible(documentDTO.getResponsibleId()));
        setNestedParameters(documentDTO, document);

        followCreatedDocument(document, creator);
        documentProducer.create(document, creator);
        return mapToDto(documentRepository.save(document));
    }

    private void followCreatedDocument(Document document, Employee creator) {
        document.addFollower(creator, false);
        documentProducer.follow(document, creator, false, true);
    }

    @Override
    @Transactional
    public GetDocumentDTO update(@NonNull DocumentDTO documentDTO, @NonNull UserDetails userDetails) {
        Document document = documentRepository.findById(documentDTO.getId())
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentDTO.getId().toString()));

        modelMapper.map(documentDTO, document);
        Customer customer = resolveCustomerByUsername(userDetails.getUsername());
        documentDtoValidator.validate(documentDTO, ImmutableMap.of("customerId", customer.getId()), documentDTO.getValidationGroups());
        Employee creator = employeeRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, userDetails.getUsername()));

        setNestedParameters(documentDTO, document);

        Long responsibleId = documentDTO.getResponsibleId();
        if (!responsibleId.equals(document.getResponsible().getId())) {
            Employee responsible = resolveResponsible(responsibleId);
            document.setResponsible(responsible);
            documentProducer.edit(document, creator);
        }

        return mapToDto(documentRepository.save(document));
    }

    private void setNestedParameters(@NonNull DocumentDTO documentDTO, Document document) {
        document.setTrades(resolveTrade(documentDTO.getTradeIds()));
        document.setLocations(resolveLocations(documentDTO.getLocationIds()));
        document.setCategory(resolveCategory(documentDTO.getCategoryId()));
        document.setSubcategory(resolveSubcategory(documentDTO.getSubcategoryId()));
        if (documentDTO.getStatus() == DocumentStatus.ACTIVE_WITH_REVIEW) {
            document.setReviewTemplate(getReviewTemplate());
        } else {
            document.eraseReviewDetails();
        }
    }

    private Set<Trade> resolveTrade(Set<Long> tradeIds) {
        return new HashSet<>(tradeRepository.findAllById(tradeIds));
    }

    private Set<Location> resolveLocations(Set<Long> locationIds) {
        return new HashSet<>(locationRepository.findAllById(locationIds));
    }

    private DocumentCategory resolveCategory(@NonNull Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));
    }

    private Employee resolveResponsible(@NonNull Long responsibleId) {
        return employeeRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, responsibleId));
    }

    private DocumentCategory resolveSubcategory(@NonNull Long subcategoryId) {
        return categoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_SUBCATEGORY_ID_NOT_FOUND, subcategoryId.toString()));
    }

    private ReviewTemplate getReviewTemplate() {
        return reviewTemplateService.getByName(ReviewTemplateService.DOCUMENT_QUICK_REVIEW_TEMPLATE_NAME)
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long documentId, @NonNull ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);

        document.setArchived(archivedStatusDTO.isArchived());
        document.setArchiveReason(archivedStatusDTO.getArchiveReason());
        documentRepository.save(document);
        if (archivedStatusDTO.isArchived()) {
            documentProducer.archive(document, creator);
        } else {
            documentProducer.unarchive(document, creator);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countNewDocuments(@NonNull UserDetails user) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));


        Predicate newDocumentsPredicate = ExpressionUtils.allOf(CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()), getNewDocumentsPredicate(employee));
        return documentRepository.count(newDocumentsPredicate);
    }

    private Predicate getNewDocumentsPredicate(Employee employee) {
        return ExpressionUtils.allOf(
                DOCUMENTS_ARCHIVED.apply(Boolean.FALSE),
                ExpressionUtils.notIn(Expressions.constant(employee), QDocument.document.visitedBy));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentPreviewDTO> getPreviewDtoById(Long documentId, boolean isSaveLog, UserDetails creatorDetails) {
        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        documentProducer.read(document, creator, isSaveLog);
        return documentRepository.findById(documentId).map(this::mapToPreviewDto);
    }

    @Override
    @Transactional
    public void changeReadStatus(Long documentId, UserDetails currentUser, DocumentReadStatusDTO documentReadStatusTO) {
        Employee currentEmployee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        FollowedDocumentId id = new FollowedDocumentId(currentEmployee.getId(), document.getId());

        FollowedDocument followedDocument = followedDocumentRepository.findById(id)
                .orElseThrow(IllegalStateException::new);
        followedDocument.setRead(documentReadStatusTO.isRead());
        followedDocumentRepository.save(followedDocument);
    }

    private DocumentPreviewDTO mapToPreviewDto(Document document) {
        return modelMapper.map(document, DocumentPreviewDTO.class);
    }

    @Override
    @Transactional
    public DocumentCategoryDTO createCategory(DocumentCategoryDTO categoryDTO) {
        categoryDtoValidator.validate(categoryDTO);

        DocumentCategory category = modelMapper.map(categoryDTO, DocumentCategory.class);
        category = categoryRepository.save(category);
        return modelMapper.map(category, DocumentCategoryDTO.class);
    }

    @Override
    @Transactional
    public DocumentCategoryDTO updateCategory(DocumentCategoryDTO categoryDTO) {
        categoryDtoValidator.validate(categoryDTO);

        Long categoryId = requireNonNull(categoryDTO.getId());
        DocumentCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));

        modelMapper.map(categoryDTO, category);
        category = categoryRepository.save(category);
        return modelMapper.map(category, DocumentCategoryDTO.class);
    }

    @Override
    @Transactional
    public List<IdAndLocalizedName> getCategories() {
        return categoryRepository.findAllByType(DocumentCategoryType.CATEGORY).stream().map(this::mapCategoryToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<IdAndLocalizedName> getSubcategories() {
        return categoryRepository.findAllByType(DocumentCategoryType.SUBCATEGORY).stream().map(this::mapCategoryToDto).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteCategory(Long id) {
        DocumentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, id.toString()));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Workbook exportToExcel(UserDetails currentUserDetails, String searchString, Predicate predicate, boolean isNew) {
        List<EntitySheet> documentSheets = new ArrayList<>();
        Predicate predicateForDocuments = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(false));
        documentSheets.add(new DocumentsEntitySheet(this, currentUserDetails, searchString, predicateForDocuments, isNew, "Documents"));
        if (isPredicateArchived(predicate)) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(true));
            documentSheets.add(new DocumentsEntitySheet(this, currentUserDetails, searchString, predicateForArchived, false, "Archived"));
        }
        return excelExportService.exportSheets(documentSheets);
    }

    @Override
    @Transactional
    public void follow(Long documentId, UserDetails currentUser, DocumentFollowDTO documentFollowDTO) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));

        Employee follower;
        boolean forced = false;
        if (documentFollowDTO.getFollowerId() != null) {
            follower = employeeRepository.findById(documentFollowDTO.getFollowerId()).orElseThrow(IllegalStateException::new);
            forced = true;
        } else {
            follower = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);
        }
        if (documentFollowDTO.isFollowed()) {
            document.addFollower(follower, forced);
        } else {
            document.removeFollower(follower);
        }
        documentProducer.follow(document, follower, forced, documentFollowDTO.isFollowed());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetFollowedDocumentDTO> getFollowedDocument(Long employeeId, Pageable pageable) {
        return employeeRepository.findAllFollowedDocumentByEmployeePaged(employeeId, pageable).map(this::mapToFollowDto);
    }

    private GetFollowedDocumentDTO mapToFollowDto(FollowedDocument document) {
        return modelMapper.map(document, GetFollowedDocumentDTO.class);
    }


    private Customer resolveCustomerByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .map(Employee::getCustomer)
                .orElseThrow(() -> new NotFoundException(USER_NAME_NOT_FOUND, username));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeByDocumentReadStatusDTO> getReadStatusDocument(Predicate predicate, Pageable pageable) {
        return followedDocumentRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    private EmployeeByDocumentReadStatusDTO mapToDto(FollowedDocument followedDocument) {
        return modelMapper.map(followedDocument, EmployeeByDocumentReadStatusDTO.class);
    }

    //region GET/DELETE standard API

    private GetDocumentDTO mapToDto(Document document) {
        return modelMapper.map(document, GetDocumentDTO.class);
    }

    @Override
    public List<GetDocumentDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetDocumentDTO> getAll(@NonNull Pageable pageable) {
        return documentRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public List<GetDocumentDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetDocumentDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return documentRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetDocumentDTO> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetDocumentDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew) {
        QDocument qDocument = QDocument.document;
        Employee employee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);
        JPAQuery query = getAllByCustomerForPreviewAndExport(currentUser, searchString, predicate, isNew);

        List<Sort.Order> sorts = pageable.getSort().get().collect(Collectors.toList());

        List<OrderSpecifier> sortParams = new LinkedList<>();
        if (sorts.size() > 0) {
            sorts.forEach(order -> {
                sortParams.add(new OrderSpecifier(Order.valueOf(order.getDirection().name()), Expressions.stringPath(qDocument, order.getProperty())));
            });

            query.orderBy(sortParams.toArray(new OrderSpecifier[sortParams.size()]));
        }

        long count = query.fetchCount();

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());


        Page<Document> result = new PageImpl<>(query.fetch(), pageable, count);
        return result.map(document -> {
            GetDocumentDTO dto = mapToDto(document);
            dto.setRead(document.getRead(employee.getId()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentExportDTO> getAllForExport(UserDetails creatorDetails, String searchString, Predicate predicate, boolean isNew) {
        QDocument document = QDocument.document;

        JPAQuery query = getAllByCustomerForPreviewAndExport(creatorDetails, searchString, predicate, isNew);
        query.orderBy(document.name.asc());

        List<Document> resultList = query.fetch();
        return resultList.stream().map(this::mapToExportDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<GetDocumentDTO> getById(@NonNull Long id) {
        return documentRepository.findById(id).map(this::mapToEditDto);
    }

    @Override
    public List<GetDocumentDTO> getAllById(@NonNull Iterable<Long> ids) {
        return documentRepository.findAllById(ids).stream().map(this::mapToEditDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(@NonNull Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(@NonNull Iterable<Long> documentIds) {
        documentRepository.deleteByIdIn(documentIds);
    }

    private DocumentEditDTO mapToEditDto(Document document) {
        return modelMapper.map(document, DocumentEditDTO.class);
    }

    private DocumentExportDTO mapToExportDto(Document document) {
        return modelMapper.map(document, DocumentExportDTO.class);
    }

    private IdAndLocalizedName mapCategoryToDto(DocumentCategory category) {
        return modelMapper.map(category, IdAndLocalizedName.class);
    }

    JPAQuery getAllByCustomerForPreviewAndExport(UserDetails currentUser, String searchString, Predicate predicate, boolean isNew) {
        QDocument document = QDocument.document;

        JPAQuery query = new JPAQuery<>(entityManager);
        query.from(document);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(searchString)) {
            predicates = Stream.of(
                    Pair.of(document.name, searchString),
                    Pair.of(document.idNumber, searchString),
                    Pair.of(document.category.nameEn, searchString),
                    Pair.of(document.subcategory.nameEn, searchString),
                    Pair.of(document.responsible.firstName, searchString),
                    Pair.of(document.responsible.lastName, searchString),
                    Pair.of(document.trades.any().nameEn, searchString),
                    Pair.of(document.locations.any().name, searchString),
                    Pair.of((EnumPath) document.type.getRoot(), searchString),
                    Pair.of((EnumPath) document.source.getRoot(), searchString),
                    Pair.of((EnumPath) document.status.getRoot(), searchString)
            ).map(predicateBuilderAndParser::toPredicate)
                    .collect(Collectors.toList());

            query.leftJoin(document.category, QDocumentCategory.documentCategory)
                    .leftJoin(document.subcategory, QDocumentCategory.documentCategory)
                    .leftJoin(document.trades, QTrade.trade)
                    .leftJoin(document.locations , QLocation.location)
                    .leftJoin(document.responsible , QEmployee.employee);
        }

        Employee employee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);

        Predicate combinePredicate = ExpressionUtils.and(ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId())), ExpressionUtils.anyOf(predicates));


        if (isPredicateArchived(predicate) && isNew) {
            combinePredicate = ExpressionUtils.allOf(combinePredicate,
                    getNewDocumentsPredicate(employee));
        }

        query.where(combinePredicate);

        return query;
    }

    boolean isPredicateArchived(Predicate predicate) {

        QDocument document = QDocument.document;
        List<Expression<?>> argsList = predicateBuilderAndParser.getArgs(predicate);
        if (argsList.size() > 0) {
            return (argsList.get(argsList.indexOf(document.archived) + 1).toString() != "true");
        }
        return false;
    }
}
