package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import dk.ledocsystem.data.model.document.FollowedDocument;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentFollowDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentReadStatusDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.document.EmployeeByDocumentReadStatusDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetFollowedDocumentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;


@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO create(@RequestBody DocumentDTO documentDTO, @CurrentUser UserDetails currentUser) {
        return documentService.create(documentDTO, currentUser);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO update(@RequestBody DocumentDTO documentDTO,
                                 @PathVariable long id,
                                 @CurrentUser UserDetails currentUser) {
        documentDTO.setId(id);
        return documentService.update(documentDTO, currentUser);
    }

    @PostMapping("/{id}/archive")
    public void changeArchivedStatus(@PathVariable Long id,
                                     @RequestBody ArchivedStatusDTO archivedStatusDTO,
                                     @CurrentUser UserDetails currentUser) {
        documentService.changeArchivedStatus(id, archivedStatusDTO, currentUser);
    }

    @GetMapping(path = "/{id}")
    public GetDocumentDTO getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id));
    }

    @GetMapping("/{id}/preview")
    public DocumentPreviewDTO getDocumentByIdForPreview(@PathVariable Long id,
                                                        @RequestParam(value = "savelog", required = false) boolean isSaveLog,
                                                        @CurrentUser UserDetails currentUser) {
        return documentService.getPreviewDtoById(id, isSaveLog, currentUser)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id.toString()));
    }

    @GetMapping("/new")
    public Iterable<GetDocumentDTO> getNewDocumentsForCurrentUser(@CurrentUser UserDetails currentUser,
                                                                  @QuerydslPredicate(root = Document.class) Predicate predicate,
                                                                  Pageable pageable) {
        return documentService.getNewDocument(currentUser, pageable, predicate);
    }

    @GetMapping
    public Iterable<GetDocumentDTO> getAllDocument(@CurrentUser UserDetails currentUser,
                                                   @QuerydslPredicate(root = Document.class) Predicate predicate,
                                                   @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return documentService.getAllByCustomer(customerId, predicate, pageable, currentUser);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable long id) {
        documentService.deleteById(id);
    }

    @PostMapping("/follow/{id}")
    public void follow(@PathVariable Long id, @CurrentUser UserDetails currentUser, DocumentFollowDTO documentFollowDTO) {
        documentService.follow(id, currentUser, documentFollowDTO);
    }

    @GetMapping("/followed")
    public Iterable<GetFollowedDocumentDTO> getFollowedDocument(@RequestParam("employeeId") Long employeeId,
                                                                Pageable pageable) {
        return documentService.getFollowedDocument(employeeId, pageable);
    }

    @PutMapping("/{documentId}/followed")
    public void changeReadStatus(@PathVariable Long documentId,
                                 @CurrentUser UserDetails currentUser,
                                 @RequestBody DocumentReadStatusDTO documentReadStatusTO) {
        documentService.changeReadStatus(documentId, currentUser, documentReadStatusTO);
    }

    @GetMapping("/followed/employees")
    public Iterable<EmployeeByDocumentReadStatusDTO> haveReadDocument(
            @QuerydslPredicate(root = FollowedDocument.class) Predicate predicate,
            @PageableDefault(sort = "employee", direction = Sort.Direction.ASC) Pageable pageable) {
        return documentService.getReadStatusDocument(predicate, pageable);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/categories")
    public DocumentCategoryDTO createCategory(@RequestBody DocumentCategoryDTO category) {
        category.setType(DocumentCategoryType.CATEGORY);
        return documentService.createCategory(category);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/categories/{categoryId}")
    public DocumentCategoryDTO updateCategory(@RequestBody DocumentCategoryDTO category, @PathVariable Long categoryId) {
        category.setId(categoryId);
        category.setType(DocumentCategoryType.CATEGORY);
        return documentService.updateCategory(category);
    }

    @GetMapping(path = "/categories")
    public Page<IdAndLocalizedName> getDocumentCategories() {
        return new PageImpl<>(documentService.getCategories());
    }

    @GetMapping(path = "/subcategories")
    public Page<IdAndLocalizedName> getDocumentSubcategories() {
        return new PageImpl<>(documentService.getSubcategories());
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        documentService.deleteCategory(categoryId);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/subcategories")
    public DocumentCategoryDTO createSubcategory(@RequestBody DocumentCategoryDTO subcategory) {
        subcategory.setType(DocumentCategoryType.SUBCATEGORY);
        return documentService.createCategory(subcategory);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/subcategories/{subcategoryId}")
    public DocumentCategoryDTO updateSubcategory(@RequestBody DocumentCategoryDTO subcategory,
                                                 @PathVariable Long subcategoryId) {
        subcategory.setId(subcategoryId);
        subcategory.setType(DocumentCategoryType.SUBCATEGORY);
        return documentService.updateCategory(subcategory);
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "subcategories/{subcategoryId}")
    public void deleteSubcategory(@PathVariable Long subcategoryId) {
        documentService.deleteCategory(subcategoryId);
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportDocuments(@CurrentUser UserDetails currentUser,
                                                                 @QuerydslPredicate(root = Document.class) Predicate predicate,
                                                                 @RequestParam(value = "new", required = false, defaultValue = "false") boolean isNew,
                                                                 @RequestParam(value = "isarchived", required = false, defaultValue = "false") boolean isArchived) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"All documents.xlsx\"")
                .body(outputStream -> documentService.exportToExcel(currentUser, predicate, isNew, isArchived).write(outputStream));
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
