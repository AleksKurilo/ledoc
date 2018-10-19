package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Document;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.repository.DocumentRepository;
import dk.ledocsystem.ledoc.service.DocumentService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final ModelMapper modelMapper;
    private final BaseValidator<DocumentDTO> documentDtoValidator;

    @Override
    @Transactional
    public Document createOrUpdate(DocumentDTO documentDTO) {
        documentDtoValidator.validate(documentDTO);

        Document document = modelMapper.map(documentDTO, Document.class);

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
    @Transactional(readOnly = true)
    public Optional<Document> getById(long id) {
        return documentRepository.findById(id);
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

    @Override
    public void deleteById(long id) {
        documentRepository.deleteById(id);
    }
}
