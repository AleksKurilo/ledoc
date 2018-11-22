package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.*;
import dk.ledocsystem.data.projections.IdAndLocalizedName;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.equipment.AuthenticationType;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.equipment.EquipmentCategory;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.EquipmentService;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<GetEquipmentDTO> getAllEquipments(@CurrentUser UserDetails currentUser, Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return equipmentService.getAllByCustomer(customerId, pageable);
    }

    @GetMapping("/filter")
    public Iterable<GetEquipmentDTO> getAllFilteredEquipments(@CurrentUser UserDetails currentUser,
                                                              @QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                              @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return equipmentService.getAllByCustomer(customerId, predicate, pageable);
    }

    @GetMapping("/new")
    public Iterable<GetEquipmentDTO> getNewEquipmentsForCurrentUser(@CurrentUser UserDetails currentUser, Pageable pageable) {
        return equipmentService.getNewEquipment(currentUser, pageable);
    }

    @GetMapping("/new/filter")
    public Iterable<GetEquipmentDTO> getNewEquipmentsForCurrentUser(@CurrentUser UserDetails currentUser,
                                                                    @QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                                    Pageable pageable) {
        return equipmentService.getNewEquipment(currentUser, pageable, predicate);
    }

    @GetMapping("/{equipmentId}")
    public GetEquipmentDTO getEquipmentById(@PathVariable Long equipmentId) {
        return equipmentService.getById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
    }

    @GetMapping("/{equipmentId}/preview")
    public EquipmentPreviewDTO getEquipmentByIdForPreview(@PathVariable Long equipmentId) {
        return equipmentService.getPreviewDtoById(equipmentId)
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
    public GetEquipmentDTO createEquipment(@RequestBody EquipmentDTO equipmentDTO, @CurrentUser UserDetails currentUser) {
        return equipmentService.createEquipment(equipmentDTO, currentUser);
    }

    @PutMapping(value = "/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetEquipmentDTO updateEquipmentById(@PathVariable Long equipmentId, @RequestBody EquipmentDTO equipmentDTO,
                                               @CurrentUser UserDetails currentUser) {
        equipmentDTO.setId(equipmentId);
        return equipmentService.updateEquipment(equipmentDTO, currentUser);
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

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
