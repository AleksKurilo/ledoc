package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.EquipmentRepository;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.GetDocumentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.repository.DocumentRepository;
import dk.ledocsystem.service.api.DocumentService;
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

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.USER_NAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final EquipmentRepository equipmentRepository;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;

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
        return mapToDto(documentRepository.save(document));
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
        return modelMapper.map(document, GetDocumentDTO.class);
    }

    //endregion
}
