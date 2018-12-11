package dk.ledocsystem.api.controller;

import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentSubcategoryDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentSubcategoryDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public void changeArchivedStatus(@PathVariable Long documentId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        documentService.changeArchivedStatus(documentId, archivedStatusDTO);
    }

    @GetMapping(path = "/{id}")
    public GetDocumentDTO getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id));
    }

    @GetMapping(path = "/employeeId/{employeeId}")
    public Set<GetDocumentDTO> getByEmployeeId(@PathVariable long employeeId) {
        return documentService.getByEmployeeId(employeeId);
    }

    @GetMapping(path = "/equipmentId/{equipmentId}")
    public Set<GetDocumentDTO> getByEquipmentId(@PathVariable long equipmentId) {
        return documentService.getByEquipmentId(equipmentId);
    }

    @GetMapping
    public Iterable<GetDocumentDTO> getAllDocument(@CurrentUser UserDetails currentUser, Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return documentService.getAllByCustomer(customerId, pageable);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable long id) {
        documentService.deleteById(id);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/category")
    public DocumentCategoryDTO createCategory(@RequestBody DocumentCategoryDTO category) {
        return documentService.createCategory(category);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/category/{id}")
    public DocumentCategoryDTO updateCategory(@RequestBody DocumentCategoryDTO category, @PathVariable Long id) {
        category.setId(id);
        return documentService.updateCategory(category);
    }

    @GetMapping(path = "/category/{id}")
    public DocumentCategoryDTO getCategory(@PathVariable Long id) {
        return documentService.getCategory(id);
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "/category/{id}")
    public void deleteCategory(@PathVariable Long id) {
        documentService.deleteCategory(id);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/category/{categoryId}/subcategory")
    public GetDocumentSubcategoryDTO createSubcategory(@RequestBody DocumentSubcategoryDTO subcategory, @PathVariable Long categoryId) {
        subcategory.setCategoryId(categoryId);
        return documentService.createSubcategory(subcategory);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/category/{categoryId}/subcategory/{id}")
    public GetDocumentSubcategoryDTO updateSubcategory(@RequestBody DocumentSubcategoryDTO subcategory,
                                                       @PathVariable Long id,
                                                       @PathVariable Long categoryId) {
        subcategory.setId(id);
        subcategory.setCategoryId(categoryId);
        return documentService.updateSubcategory(subcategory);
    }

    @GetMapping(path = "/category/subcategory/{id}")
    public GetDocumentSubcategoryDTO getSubcategory(@PathVariable Long id) {
        return documentService.getSubcategory(id);
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "/category/subcategory/{id}")
    public void deleteSubcategory(@PathVariable Long id) {
        documentService.deleteSubcategory(id);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
