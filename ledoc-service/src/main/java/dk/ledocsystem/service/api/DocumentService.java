package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentFollowDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentReadStatusDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.document.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface DocumentService extends CustomerBasedDomainService<GetDocumentDTO> {

    GetDocumentDTO create(DocumentDTO documentDTO, UserDetails creator);

    GetDocumentDTO update(DocumentDTO documentDTO, UserDetails currentUserDetails);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long documentId, ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails);

    long countNewDocuments(UserDetails user);

    void changeReadStatus(Long documentId, UserDetails creator, DocumentReadStatusDTO documentReadStatusTO);

    Page<GetDocumentDTO> getNewDocument(UserDetails user, Pageable pageable, Predicate predicate);

    Optional<DocumentPreviewDTO> getPreviewDtoById(Long documentId, boolean isSaveLog, UserDetails creatorDetails);

    Page<GetDocumentDTO> getAllByCustomer(Long customerId, Predicate predicate, Pageable pageable, UserDetails creatorDetails);

    void follow(Long documentId, UserDetails currentUser, DocumentFollowDTO documentFollowDTO);

    Page<GetFollowedDocumentDTO> getFollowedDocument(Long employeeId, Pageable pageable);

    Page<EmployeeByDocumentReadStatusDTO> getReadStatusDocument(Predicate predicate, Pageable pageable);

    DocumentCategoryDTO createCategory(DocumentCategoryDTO category);

    DocumentCategoryDTO updateCategory(DocumentCategoryDTO category);

    List<IdAndLocalizedName> getCategories();

    List<IdAndLocalizedName> getSubcategories();

    void deleteCategory(Long id);

    List<DocumentExportDTO> getAllForExport(UserDetails creatorDetails, Predicate predicate, boolean isNew);

    Workbook exportToExcel(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived);
}
