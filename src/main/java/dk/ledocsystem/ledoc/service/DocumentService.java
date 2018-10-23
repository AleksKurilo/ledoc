package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Document;

import java.util.Set;

public interface DocumentService extends CustomerBasedDomainService<Document> {

    Document createOrUpdate(DocumentDTO documentDTO, Customer customer);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long documentId, ArchivedStatusDTO archivedStatusDTO);

    Set<Document> getByEmployeeId(long employeeId);

    Set<Document> getByEquipmentId(long equipmentId);
}
