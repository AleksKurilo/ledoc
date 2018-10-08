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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Document createOrUpdate(DocumentDTO documentDTO) {
        Document document = modelMapper.map(documentDTO, Document.class);

        Long employeeId = documentDTO.getEmployeeId();
        if (employeeId != null) {
            Employee employee = employeeService.getById(employeeId)
                    .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
            document.setEmployee(employee);
        }

        Long equipmentId = documentDTO.getEquipmentId();
        if (equipmentId != null) {
            Equipment equipment = equipmentService.getById(equipmentId)
                    .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));
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
    public Set<Document> getByEmployeeId(long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Set<Document> getByEquipmentId(long equipmentId) {
        return documentRepository.findByEquipmentId(equipmentId);
    }

    @Override
    public void deleteById(long id) {
        documentRepository.deleteById(id);
    }


}
