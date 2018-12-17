package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.Set;

public interface DocumentService extends CustomerBasedDomainService<GetDocumentDTO> {

    GetDocumentDTO createOrUpdate(DocumentDTO documentDTO, UserDetails creator);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long documentId, ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails);

    Set<GetDocumentDTO> getByEmployeeId(long employeeId);

    Set<GetDocumentDTO> getByEquipmentId(long equipmentId);

    Page<GetDocumentDTO> getNewDocument(UserDetails user, Pageable pageable);

    Page<GetDocumentDTO> getNewDocument(UserDetails user, Pageable pageable, Predicate predicate);

    Optional<DocumentPreviewDTO> getPreviewDtoById(Long documentId, boolean isSaveLog, UserDetails creatorDetails);

    DocumentCategoryDTO createCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO updateCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO getCategory(Long id);

    Set<DocumentCategoryDTO> getAllCategory();

    Set<DocumentCategoryDTO> getAllSubcategory();

    void deleteCategory(Long id);

    @Transactional(readOnly = true)
    List<List<String>> getAllForExport(UserDetails creatorDetails, Predicate predicate, boolean isNew);
}
