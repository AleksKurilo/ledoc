package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.SupplierCategoryService;
import dk.ledocsystem.service.api.SupplierService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.supplier.GetSupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.supplier.SupplierPreviewDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier")
public class SupplierController {

    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final SupplierCategoryService supplierCategoryService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetSupplierDTO create(@RequestBody SupplierDTO supplierDTO, @CurrentUser UserDetails currentUser) {
        return supplierService.create(supplierDTO, currentUser);
    }

    @PostMapping(path = "/{id}/archive")
    public void changeArchivedStatus(@PathVariable Long id, @RequestBody ArchivedStatusDTO archivedStatusDTO,
                                     @CurrentUser UserDetails currentUser) {
        supplierService.changeArchivedStatus(id, archivedStatusDTO, currentUser);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetSupplierDTO update(@RequestBody SupplierDTO supplierDTO,
                                 @PathVariable long id,
                                 @CurrentUser UserDetails currentUser) {
        supplierDTO.setId(id);
        return supplierService.update(supplierDTO, currentUser);
    }

    @GetMapping(path = "/{id}")
    public GetSupplierDTO getById(@PathVariable long id) {
        return supplierService.getById(id)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, id));
    }

    @GetMapping(path = "/{id}/preview")
    public SupplierPreviewDTO getSupplierByIdForPreview(@PathVariable Long id,
                                                        @RequestParam(value = "savelog", required = false) boolean isSaveLog,
                                                        @CurrentUser UserDetails currentUser) {
        return supplierService.getPreviewDtoById(id, isSaveLog, currentUser)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, id.toString()));
    }

    @GetMapping
    public Iterable<GetSupplierDTO> getAllSupplier(@CurrentUser UserDetails currentUser,
                                                   @QuerydslPredicate(root = Supplier.class) Predicate predicate,
                                                   @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return supplierService.getAllByCustomer(customerId, predicate, pageable);
    }

    @RolesAllowed("super_admin")
    @PostMapping(path = "/categories")
    public IdAndLocalizedName createCategory(@RequestBody SupplierCategoryDTO categoryDTO) {
        return supplierCategoryService.create(categoryDTO);
    }

    @RolesAllowed("super_admin")
    @PutMapping(path = "/categories/{categoryId}")
    public IdAndLocalizedName updateCategory(@PathVariable Long categoryId, @RequestBody SupplierCategoryDTO categoryDTO) {
        return supplierCategoryService.update(categoryId, categoryDTO);
    }

    @GetMapping(path = "/categories")
    public Page<IdAndLocalizedName> getCategories() {
        return new PageImpl<>(supplierCategoryService.getList());
    }

    @RolesAllowed("super_admin")
    @DeleteMapping(path = "/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        supplierCategoryService.delete(categoryId);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
