package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.equipment.AuthenticationTypeDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCategoryCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentLoanDTO;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.equipment.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentService extends CustomerBasedDomainService<Equipment> {

    /**
     * Creates new {@link Equipment}, using the data from {@code equipmentCreateDTO}.
     *
     * @param equipmentCreateDTO Equipment properties
     * @param customer           Customer - the owner of equipment
     * @return Newly created {@link Equipment}
     */
    Equipment createEquipment(EquipmentCreateDTO equipmentCreateDTO, Customer customer);

    /**
     * Updates the properties of the equipment with the given ID with properties of {@code equipmentCreateDTO}.
     *
     * @param equipmentId      ID of the equipment
     * @param equipmentEditDTO New properties of the equipment
     * @return Updated {@link Equipment}
     */
    Equipment updateEquipment(Long equipmentId, EquipmentEditDTO equipmentEditDTO);

    Page<Equipment> getNewEquipment(Long userId, Pageable pageable);

    Page<Equipment> getNewEquipment(Long userId, Pageable pageable, Predicate predicate);

    /**
     * @return All equipment eligible for review
     */
    List<Equipment> getAllForReview();

    void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO);

    void returnLoanedEquipment(Long equipmentId);

    List<IdAndLocalizedName> getAuthTypes();

    AuthenticationType createAuthType(AuthenticationTypeDTO authenticationTypeDTO);

    EquipmentCategory createNewCategory(EquipmentCategoryCreateDTO categoryCreateDTO);
}
