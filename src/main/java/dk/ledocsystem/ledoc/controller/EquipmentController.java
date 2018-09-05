package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.equipment.AuthenticationTypeDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public Iterable<Equipment> getAllEquipments(Pageable pageable) {
        return equipmentService.getAll(pageable);
    }

    @GetMapping("/filter")
    public Iterable<Equipment> getAllFilteredEquipments(@QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                        Pageable pageable) {
        return equipmentService.getAll(predicate, pageable);
    }

    @GetMapping("/new")
    public Iterable<Equipment> getNewEquipmentsForCurrentUser(Pageable pageable) {
        return equipmentService.getNewEquipment(pageable);
    }

    @GetMapping("/new/filter")
    public Iterable<Equipment> getNewEquipmentsForCurrentUser(@QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                              Pageable pageable) {
        return equipmentService.getNewEquipment(pageable, predicate);
    }

    @GetMapping("/{equipmentId}")
    public Equipment getEquipmentById(@PathVariable Long equipmentId) {
        return equipmentService.getById(equipmentId)
                .orElseThrow(() -> new NotFoundException("equipment.id.not.found", equipmentId.toString()));
    }

    @GetMapping("/auth-types")
    public List<IdAndLocalizedName> getAuthenticationTypes() {
        return equipmentService.getAuthTypes();
    }

    @RolesAllowed("ROLE_super_admin")
    @PostMapping("/auth-types")
    public AuthenticationType createAuthType(@RequestBody @Valid AuthenticationTypeDTO authenticationTypeDTO) {
        return equipmentService.createAuthType(authenticationTypeDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Equipment createEquipment(@RequestBody @Valid EquipmentCreateDTO equipmentCreateDTO) {
        return equipmentService.createEquipment(equipmentCreateDTO);
    }

    @PutMapping(value = "/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Equipment updateEquipmentById(@PathVariable Long equipmentId,
                                         @RequestBody @Valid EquipmentEditDTO equipmentEditDTO) {
        return equipmentService.updateEquipment(equipmentId, equipmentEditDTO);
    }

    @DeleteMapping("/{equipmentId}")
    public void deleteById(@PathVariable Long equipmentId) {
        equipmentService.deleteById(equipmentId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        equipmentService.deleteByIds(ids);
    }
}
