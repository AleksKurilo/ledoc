package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.equipment.AuthenticationTypeDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentCategoryCreateDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentDTO;
import dk.ledocsystem.ledoc.dto.equipment.EquipmentLoanDTO;
import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.equipment.AuthenticationType;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import dk.ledocsystem.ledoc.service.dto.EquipmentPreviewDTO;
import dk.ledocsystem.ledoc.service.dto.GetEquipmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EquipmentService extends CustomerBasedDomainService<Equipment> {

    /**
     * Creates new {@link Equipment}, using the data from {@code equipmentDTO}.
     *
     * @param equipmentDTO Equipment properties
     * @param customer     Customer - the owner of equipment
     * @return Newly created {@link Equipment}
     */
    Equipment createEquipment(EquipmentDTO equipmentDTO, Customer customer);

    /**
     * Updates the properties of the equipment with the given ID with properties of {@code equipmentCreateDTO}.
     *
     * @param equipmentDTO New properties of the equipment
     * @return Updated {@link Equipment}
     */
    Equipment updateEquipment(EquipmentDTO equipmentDTO);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long equipmentId, ArchivedStatusDTO archivedStatusDTO);

    Page<Equipment> getNewEquipment(Long userId, Pageable pageable);

    Page<Equipment> getNewEquipment(Long userId, Pageable pageable, Predicate predicate);

    /**
     * @return All equipment eligible for review
     */
    List<Equipment> getAllForReview();

    // TODO This is shit.
    // TODO Replace it with getById during service layer separation process.
    Optional<GetEquipmentDTO> getEquipmentDtoById(Long equipmentId);

    Optional<EquipmentPreviewDTO> getPreviewDtoById(Long equipmentId);

    void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO);

    void returnLoanedEquipment(Long equipmentId);

    List<IdAndLocalizedName> getAuthTypes();

    Page<IdAndLocalizedName> getAuthTypes(Pageable pageable);

    AuthenticationType createAuthType(AuthenticationTypeDTO authenticationTypeDTO);

    List<IdAndLocalizedName> getCategories();

    Page<IdAndLocalizedName> getCategories(Pageable pageable);

    EquipmentCategory createNewCategory(EquipmentCategoryCreateDTO categoryCreateDTO);
}
