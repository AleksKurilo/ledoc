package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Document;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.repository.DocumentRepository;
import dk.ledocsystem.ledoc.service.DocumentService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final DocumentRepository documentRepository;
    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;

    @Override
    @Transactional
    public Document createOrUpdate(DocumentDTO documentDTO, Customer customer) {
        documentDtoValidator.validate(documentDTO);

        Document document = modelMapper.map(documentDTO, Document.class);
        document.setCustomer(customer);

        Long documentId = documentDTO.getId();
        if (documentId != null) {
            documentRepository.findById(documentId)
                    .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        }

        Long employeeId = documentDTO.getEmployeeId();
        if (employeeId != null) {
            Employee employee = employeeService.getById(employeeId)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
            document.setEmployee(employee);
        }

        Long equipmentId = documentDTO.getEquipmentId();
        if (equipmentId != null) {
            Equipment equipment = equipmentService.getById(equipmentId)
                    .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, equipmentId.toString()));
            document.setEquipment(equipment);
        }
        return documentRepository.save(document);
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long documentId, @NonNull ArchivedStatusDTO archivedStatusDTO) {
        Document document = getById(documentId)
                .orElseThrow(() -> new NotFoundException("document.id.not.found", documentId.toString()));

        document.setArchived(archivedStatusDTO.isArchived());
        document.setArchiveReason(archivedStatusDTO.getArchiveReason());
        documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Document> getByEmployeeId(long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Document> getByEquipmentId(long equipmentId) {
        return documentRepository.findByEquipmentId(equipmentId);
    }

    //region GET/DELETE standard API

    @Override
    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    @Override
    public Page<Document> getAll(@NonNull Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    @Override
    public List<Document> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(documentRepository.findAll(predicate));
    }

    @Override
    public Page<Document> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return documentRepository.findAll(predicate, pageable);
    }

    @Override
    public List<Document> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Document> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<Document> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Document> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return documentRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Document> getById(@NonNull Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> getAllById(@NonNull Iterable<Long> ids) {
        return documentRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(@NonNull Iterable<Long> documentIds) {
        documentRepository.deleteByIdIn(documentIds);
    }

    //endregion
}
