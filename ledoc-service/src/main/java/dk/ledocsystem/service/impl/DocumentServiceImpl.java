package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.document.*;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import dk.ledocsystem.data.repository.*;
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
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QDocument.document.customer.id, customerId);
    private static final Function<Boolean, Predicate> DOCUMENTS_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QDocument.document.archived, archived);

    private final TradeRepository tradeRepository;
    private final DocumentProducer documentProducer;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;
    private final ReviewTemplateService reviewTemplateService;
    private final DocumentCategoryRepository categoryRepository;
    private final FollowedDocumentRepository followedDocumentRepository;

    private final ExcelExportService excelExportService;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;
    private final BaseValidator<DocumentCategoryDTO> categoryDtoValidator;

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

        Predicate newDocumentsPredicate = getNewDocumentsPredicate(employee);
        return documentRepository.count(newDocumentsPredicate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getNewDocument(@NonNull UserDetails user, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = employeeRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, user.getUsername()));

        Predicate combinePredicate = ExpressionUtils.and(predicate, getNewDocumentsPredicate(employee));
        return getAll(combinePredicate, pageable);
    }

    private Predicate getNewDocumentsPredicate(Employee employee) {
        return ExpressionUtils.allOf(
                DOCUMENTS_ARCHIVED.apply(Boolean.FALSE),
                CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()),
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
    public Workbook exportToExcel(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> documentSheets = new ArrayList<>();
        Predicate predicateForDocuments = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(false));
        documentSheets.add(new DocumentsEntitySheet(this, currentUserDetails, predicateForDocuments, isNew, "Documents"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(true));
            documentSheets.add(new DocumentsEntitySheet(this, currentUserDetails, predicateForArchived, isNew, "Archived"));
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
            document.removeFollowe(follower);
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

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable, UserDetails creatorDetails) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        Long employeeId = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new).getId();

        return documentRepository.findAll(combinePredicate, pageable)
                .map(document -> {
                    GetDocumentDTO dto = mapToDto(document);
                    dto.setRead(document.getRead(employeeId));
                    return dto;
                });
    }

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
    public List<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, "", predicate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, String searchString, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return documentRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentExportDTO> getAllForExport(UserDetails creatorDetails, Predicate predicate, boolean isNew) {
        Employee employee = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Long customerId = employee.getCustomer().getId();
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        if (isNew) {
            combinePredicate = ExpressionUtils.allOf(combinePredicate,
                    ExpressionUtils.notIn(Expressions.constant(employee), QDocument.document.visitedBy));
        }
        return documentRepository.findAll(combinePredicate).stream().map(this::mapToExportDto).collect(Collectors.toList());
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

    //endregion
}
