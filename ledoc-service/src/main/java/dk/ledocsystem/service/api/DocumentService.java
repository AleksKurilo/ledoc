package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.GetDocumentDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public interface DocumentService extends CustomerBasedDomainService<GetDocumentDTO> {

    GetDocumentDTO createOrUpdate(DocumentDTO documentDTO, UserDetails creator);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long documentId, ArchivedStatusDTO archivedStatusDTO);

    Set<GetDocumentDTO> getByEmployeeId(long employeeId);

    Set<GetDocumentDTO> getByEquipmentId(long equipmentId);
}
