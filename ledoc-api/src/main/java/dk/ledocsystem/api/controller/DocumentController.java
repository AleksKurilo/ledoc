package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;


@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO create(@RequestBody DocumentDTO documentDTO, @CurrentUser UserDetails currentUser) {
        return documentService.createOrUpdate(documentDTO, currentUser);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO update(@RequestBody DocumentDTO documentDTO, @PathVariable long id, @CurrentUser UserDetails currentUser) {
        documentDTO.setId(id);
        return documentService.createOrUpdate(documentDTO, currentUser);
    }

    @PostMapping("/{documentId}/archive")
    public void changeArchivedStatus(@PathVariable Long documentId, @RequestBody ArchivedStatusDTO archivedStatusDTO, @CurrentUser UserDetails currentUser) {
        documentService.changeArchivedStatus(documentId, archivedStatusDTO, currentUser);
    }

    @GetMapping(path = "/{id}")
    public GetDocumentDTO getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id));
    }

    @GetMapping("/{id}/preview")
    public DocumentPreviewDTO getEquipmentByIdForPreview(@PathVariable Long id, @RequestParam(value = "savelog", required = false) boolean isSaveLog,
                                                         @CurrentUser UserDetails currentUser) {
        return documentService.getPreviewDtoById(id, isSaveLog, currentUser)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id.toString()));
    }

    @GetMapping(path = "/employeeId/{employeeId}")
    public Set<GetDocumentDTO> getByEmployeeId(@PathVariable long employeeId) {
        return documentService.getByEmployeeId(employeeId);
    }

    @GetMapping(path = "/equipmentId/{equipmentId}")
    public Set<GetDocumentDTO> getByEquipmentId(@PathVariable long equipmentId) {
        return documentService.getByEquipmentId(equipmentId);
    }

    @GetMapping("/new")
    public Iterable<GetDocumentDTO> getNewEquipmentsForCurrentUser(@CurrentUser UserDetails currentUser,
                                                                   @QuerydslPredicate(root = Document.class) Predicate predicate,
                                                                   Pageable pageable) {
        return documentService.getNewDocument(currentUser, pageable, predicate);
    }

    @GetMapping
    public Iterable<GetDocumentDTO> getAllDocument(@CurrentUser UserDetails currentUser,
                                                   @QuerydslPredicate(root = Document.class) Predicate predicate,
                                                   @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return documentService.getAllByCustomer(customerId, predicate, pageable);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable long id) {
        documentService.deleteById(id);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/categories")
    public DocumentCategoryDTO createCategory(@RequestBody DocumentCategoryDTO category) {
        category.setType(DocumentCategoryType.CATEGORY);
        return documentService.createCategory(category);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/categories/{id}")
    public DocumentCategoryDTO updateCategory(@RequestBody DocumentCategoryDTO category, @PathVariable Long id) {
        category.setId(id);
        category.setType(DocumentCategoryType.CATEGORY);
        return documentService.updateCategory(category);
    }

    @GetMapping(path = "/categories/{id}")
    public DocumentCategoryDTO getCategory(@PathVariable Long id) {
        return documentService.getCategory(id);
    }

    @GetMapping(path = "/categories")
    public Set<DocumentCategoryDTO> getAllCategory() {
        return documentService.getAllCategory();
    }

    @GetMapping(path = "/subcategories")
    public Set<DocumentCategoryDTO> getAllSubCategory() {
        return documentService.getAllSubcategory();
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        documentService.deleteCategory(id);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/subcategories")
    public DocumentCategoryDTO createSubcategory(@RequestBody DocumentCategoryDTO subcategory) {
        subcategory.setType(DocumentCategoryType.SUBCATEGORY);
        return documentService.createCategory(subcategory);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/subcategories/{id}")
    public DocumentCategoryDTO updateSubcategory(@RequestBody DocumentCategoryDTO subcategory,
                                                 @PathVariable Long id) {
        subcategory.setId(id);
        subcategory.setType(DocumentCategoryType.SUBCATEGORY);
        return documentService.updateCategory(subcategory);
    }

    @GetMapping(path = "subcategories/{id}")
    public DocumentCategoryDTO getSubcategory(@PathVariable Long id) {
        return documentService.getCategory(id);
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "subcategories/{id}")
    public void deleteSubcategory(@PathVariable Long id) {
        documentService.deleteCategory(id);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
