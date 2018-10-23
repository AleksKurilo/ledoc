package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.equipment.*;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.equipment.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<Equipment> getAllEquipments(Pageable pageable) {
        return equipmentService.getAllByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/filter")
    public Iterable<Equipment> getAllFilteredEquipments(@QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                        Pageable pageable) {
        return equipmentService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
    }

    @GetMapping("/new")
    public Iterable<Equipment> getNewEquipmentsForCurrentUser(Pageable pageable) {
        return equipmentService.getNewEquipment(getCurrentUserId(), pageable);
    }

    @GetMapping("/new/filter")
    public Iterable<Equipment> getNewEquipmentsForCurrentUser(@QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                              Pageable pageable) {
        return equipmentService.getNewEquipment(getCurrentUserId(), pageable, predicate);
    }

    @GetMapping("/{equipmentId}")
    public Equipment getEquipmentById(@PathVariable Long equipmentId) {
        return equipmentService.getById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
    }

    @GetMapping("/auth-types")
    public Iterable<IdAndLocalizedName> getAuthenticationTypes(Pageable pageable) {
        return equipmentService.getAuthTypes(pageable);
    }

    @RolesAllowed("ROLE_super_admin")
    @PostMapping(value = "/auth-types", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthenticationType createAuthType(@RequestBody AuthenticationTypeDTO authenticationTypeDTO) {
        return equipmentService.createAuthType(authenticationTypeDTO);
    }

    @GetMapping("/categories")
    public Iterable<IdAndLocalizedName> getCategories(Pageable pageable) {
        return equipmentService.getCategories(pageable);
    }

    @RolesAllowed("super_admin")
    @PostMapping(value = "/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EquipmentCategory createNewEqCategory(@RequestBody EquipmentCategoryCreateDTO categoryCreateDTO) {
        return equipmentService.createNewCategory(categoryCreateDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Equipment createEquipment(@RequestBody EquipmentCreateDTO equipmentCreateDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return equipmentService.createEquipment(equipmentCreateDTO, currentCustomer);
    }

    @PutMapping(value = "/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Equipment updateEquipmentById(@PathVariable Long equipmentId,
                                         @RequestBody EquipmentEditDTO equipmentEditDTO) {
        equipmentEditDTO.setId(equipmentId);
        return equipmentService.updateEquipment(equipmentEditDTO);
    }

    @PostMapping("/{equipmentId}/archive")
    public void changeArchivedStatus(@PathVariable Long equipmentId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        equipmentService.changeArchivedStatus(equipmentId, archivedStatusDTO);
    }

    @PostMapping(value = "/loan/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void loanEquipment(@PathVariable Long equipmentId,
                              @RequestBody EquipmentLoanDTO equipmentLoanDTO) {
        equipmentService.loanEquipment(equipmentId, equipmentLoanDTO);
    }

    @PostMapping(value = "/loan/{equipmentId}/return")
    public void returnLentEquipment(@PathVariable Long equipmentId) {
        equipmentService.returnLoanedEquipment(equipmentId);
    }

    @DeleteMapping("/{equipmentId}")
    public void deleteById(@PathVariable Long equipmentId) {
        equipmentService.deleteById(equipmentId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        equipmentService.deleteByIds(ids);
    }

    private Long getCurrentCustomerId() {
        return customerService.getCurrentCustomerId();
    }

    private Long getCurrentUserId() {
        return employeeService.getCurrentUser().getUserId();
}
}
