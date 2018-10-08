package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.model.Document;

import java.util.Optional;
import java.util.Set;

public interface DocumentService {

    Document createOrUpdate(DocumentDTO documentDTO);

    Optional<Document> getById(long id);

    Set<Document> getByEmployeeId(long employeeId);

    Set<Document> getByEquipmentId(long equipmentId);

    void deleteById(long id);
}
