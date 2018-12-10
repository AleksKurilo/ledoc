package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentSubcategoryDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentSubcategoryDTO;
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

    DocumentCategoryDTO createCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO updateCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO getCategory(Long id);

    void deleteCategory(Long id);

    GetDocumentSubcategoryDTO createSubcategory(DocumentSubcategoryDTO subcategory);

    GetDocumentSubcategoryDTO updateSubcategory(DocumentSubcategoryDTO subcategory);

    GetDocumentSubcategoryDTO getSubcategory(Long id);

    void deleteSubcategory(Long id);
}
