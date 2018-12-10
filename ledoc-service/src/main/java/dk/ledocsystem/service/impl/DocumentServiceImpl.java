package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.document.DocumentCategory;
import dk.ledocsystem.data.model.document.DocumentSubcategory;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.repository.*;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentSubcategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentSubcategoryDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final EquipmentRepository equipmentRepository;
    private final DocumentCategoryRepository categoryRepository;
    private final DocumentSubcategoryRepository subcategoryRepository;
    private final LocationRepository locationRepository;
    private final TradeRepository tradeRepository;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;
    private final BaseValidator<DocumentCategoryDTO> categoryDtoValidator;
    private final BaseValidator<DocumentSubcategoryDTO> subcategoryDTOBaseValidator;

    @Override
    @Transactional
    public GetDocumentDTO createOrUpdate(@NonNull DocumentDTO documentDTO, @NonNull UserDetails userDetails) {
        documentDtoValidator.validate(documentDTO);

        Document document = modelMapper.map(documentDTO, Document.class);
        Customer customer = resolveCustomerByUsername(userDetails.getUsername());
        document.setCustomer(customer);

        Long documentId = documentDTO.getId();
        if (documentId != null) {
            documentRepository.findById(documentId)
                    .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        }

        Long employeeId = documentDTO.getEmployeeId();
        if (employeeId != null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
            document.setEmployee(employee);
        }

        Long equipmentId = documentDTO.getEquipmentId();
        if (equipmentId != null) {
            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, equipmentId.toString()));
            document.setEquipment(equipment);
        }

        Long responsibleId = documentDTO.getResponsibleId();
        Employee responsible = null;
        if (responsibleId != null) {
            responsible = employeeRepository.findById(responsibleId)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        }

        Location location = locationRepository.findById(documentDTO.getLocationId())
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, documentDTO.getSubcategoryId().toString()));
        Trade trade = tradeRepository.findById(documentDTO.getTradeId())
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, documentDTO.getSubcategoryId().toString()));

        document.setResponsible(responsible);
        document.setLocation(location);
        document.setTrade(trade);
        setCategoryToDocument(document, documentDTO.getCategoryId(), documentDTO.getSubcategoryId());
        return mapToDto(documentRepository.save(document));
    }

    private void setCategoryToDocument(Document document, Long categoryId, Long cubcategoryId) {
        DocumentCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));
        DocumentSubcategory subcategory = subcategoryRepository.findById(cubcategoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_SUBCATEGORY_ID_NOT_FOUND, cubcategoryId.toString()));

        document.setCategory(category);
        document.setSubcategory(subcategory);
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long documentId, @NonNull ArchivedStatusDTO archivedStatusDTO) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));

        document.setArchived(archivedStatusDTO.isArchived());
        document.setArchiveReason(archivedStatusDTO.getArchiveReason());
        documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GetDocumentDTO> getByEmployeeId(long employeeId) {
        return documentRepository.findByEmployeeId(employeeId).stream().map(this::mapToDto).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GetDocumentDTO> getByEquipmentId(long equipmentId) {
        return documentRepository.findByEquipmentId(equipmentId).stream().map(this::mapToDto).collect(Collectors.toSet());
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
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));

        DocumentCategory category = modelMapper.map(categoryDTO, DocumentCategory.class);
        category = categoryRepository.save(category);
        return modelMapper.map(category, DocumentCategoryDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentCategoryDTO getCategory(Long id) {
        DocumentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, id.toString()));
        return modelMapper.map(category, DocumentCategoryDTO.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        DocumentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, id.toString()));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public GetDocumentSubcategoryDTO createSubcategory(DocumentSubcategoryDTO subcategoryDTO) {
        subcategoryDTOBaseValidator.validate(subcategoryDTO);

        Long categoryId = requireNonNull(subcategoryDTO.getCategoryId());
        DocumentCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));

        return setCategoryToDocumentDTO(subcategoryDTO, category);
    }

    @Override
    @Transactional
    public GetDocumentSubcategoryDTO updateSubcategory(DocumentSubcategoryDTO subcategoryDTO) {
        subcategoryDTOBaseValidator.validate(subcategoryDTO);

        Long categoryId = requireNonNull(subcategoryDTO.getCategoryId());
        DocumentCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, categoryId.toString()));
        Long subcategoryId = requireNonNull(subcategoryDTO.getId());
        subcategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_SUBCATEGORY_ID_NOT_FOUND, categoryId.toString()));

        return setCategoryToDocumentDTO(subcategoryDTO, category);
    }

    private GetDocumentSubcategoryDTO setCategoryToDocumentDTO(DocumentSubcategoryDTO subcategoryDTO, DocumentCategory category) {
        DocumentSubcategory subcategory = modelMapper.map(subcategoryDTO, DocumentSubcategory.class);
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);

        GetDocumentSubcategoryDTO getSubcategoryDTO = modelMapper.map(subcategory, GetDocumentSubcategoryDTO.class);
        getSubcategoryDTO.setCategoryDTO(modelMapper.map(category, DocumentCategoryDTO.class));
        return getSubcategoryDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public GetDocumentSubcategoryDTO getSubcategory(Long id) {
        DocumentSubcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_SUBCATEGORY_ID_NOT_FOUND, id.toString()));
        return modelMapper.map(subcategory, GetDocumentSubcategoryDTO.class);
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        DocumentSubcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_CATEGORY_ID_NOT_FOUND, id.toString()));
        subcategoryRepository.delete(subcategory);
    }

    private Customer resolveCustomerByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .map(Employee::getCustomer)
                .orElseThrow(() -> new NotFoundException(USER_NAME_NOT_FOUND, username));
    }

    //region GET/DELETE standard API

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
    public List<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetDocumentDTO> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return documentRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public Optional<GetDocumentDTO> getById(@NonNull Long id) {
        return documentRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public List<GetDocumentDTO> getAllById(@NonNull Iterable<Long> ids) {
        return documentRepository.findAllById(ids).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(@NonNull Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(@NonNull Iterable<Long> documentIds) {
        documentRepository.deleteByIdIn(documentIds);
    }

    private GetDocumentDTO mapToDto(Document document) {
        GetDocumentDTO dto = modelMapper.map(document, GetDocumentDTO.class);
        DocumentCategoryDTO category = modelMapper.map(document.getCategory(), DocumentCategoryDTO.class);
        DocumentSubcategoryDTO subcategory = modelMapper.map(document.getSubcategory(), DocumentSubcategoryDTO.class);
        EmployeeDTO responsible = modelMapper.map(document.getResponsible(), EmployeeDTO.class);
        LocationDTO location = modelMapper.map(document.getLocation(), LocationDTO.class);

        dto.setCategory(category);
        dto.setSubcategory(subcategory);
        dto.setResponsible(responsible);
        dto.setLocation(location);
        if (document.getEmployee() != null) {
            EmployeeDTO employeeDTO = modelMapper.map(document.getResponsible(), EmployeeDTO.class);
            dto.setEmployee(employeeDTO);
        }
        if (document.getEquipment() != null) {
            EquipmentDTO equipmentDTO = modelMapper.map(document.getEquipment(), EquipmentDTO.class);
            dto.setEquipment(equipmentDTO);
        }
        return dto;
    }

    //endregion
}
