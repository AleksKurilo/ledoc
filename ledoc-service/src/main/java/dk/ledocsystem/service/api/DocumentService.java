package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentExportDTO;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface DocumentService extends CustomerBasedDomainService<GetDocumentDTO> {

    GetDocumentDTO createOrUpdate(DocumentDTO documentDTO, UserDetails creator);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long documentId, ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails);

    Page<GetDocumentDTO> getNewDocument(UserDetails user, Pageable pageable);

    Page<GetDocumentDTO> getNewDocument(UserDetails user, Pageable pageable, Predicate predicate);

    Optional<DocumentPreviewDTO> getPreviewDtoById(Long documentId, boolean isSaveLog, UserDetails creatorDetails);

    DocumentCategoryDTO createCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO updateCategory(DocumentCategoryDTO category);

    List<IdAndLocalizedName> getCategories();

    List<IdAndLocalizedName> getSubcategories();

    void deleteCategory(Long id);

    List<DocumentExportDTO> getAllForExport(UserDetails creatorDetails, Predicate predicate, boolean isNew);

    Workbook exportToExcel(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived);
}
