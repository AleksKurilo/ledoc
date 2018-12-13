package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.equipment.AuthenticationType;
import dk.ledocsystem.data.model.equipment.EquipmentCategory;
import dk.ledocsystem.data.projections.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.*;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetFollowedEquipmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface EquipmentService extends CustomerBasedDomainService<GetEquipmentDTO> {

    /**
     * Creates new {@link dk.ledocsystem.data.model.equipment.Equipment}, using the data from {@code equipmentDTO}.
     *
     * @param equipmentDTO   Equipment properties
     * @param creatorDetails Creator
     * @return Newly created {@link GetEquipmentDTO equipment}
     */
    GetEquipmentDTO createEquipment(EquipmentDTO equipmentDTO, UserDetails creatorDetails);

    /**
     * Updates the properties of the equipment with the given ID with properties of {@code equipmentCreateDTO}.
     *
     * @param equipmentDTO   New properties of the equipment
     * @param creatorDetails Creator
     * @return Updated {@link GetEquipmentDTO equipment}
     */
    GetEquipmentDTO updateEquipment(EquipmentDTO equipmentDTO, UserDetails creatorDetails);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long equipmentId, ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails);

    Page<GetEquipmentDTO> getNewEquipment(UserDetails user, Pageable pageable);

    Page<GetEquipmentDTO> getNewEquipment(UserDetails user, Pageable pageable, Predicate predicate);

    Optional<EquipmentPreviewDTO> getPreviewDtoById(Long equipmentId, boolean isSaveLog, UserDetails creatorDetails);

    void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO);

    void returnLoanedEquipment(Long equipmentId);

    List<IdAndLocalizedName> getAuthTypes();

    Page<IdAndLocalizedName> getAuthTypes(Pageable pageable);

    AuthenticationType createAuthType(AuthenticationTypeDTO authenticationTypeDTO);

    List<IdAndLocalizedName> getCategories();

    Page<IdAndLocalizedName> getCategories(Pageable pageable);

    EquipmentCategory createNewCategory(EquipmentCategoryCreateDTO categoryCreateDTO);

    void follow(Long equipmentId, UserDetails currentUser, EquipmentFollowDTO equipmentFollowDTO);

    List<GetFollowedEquipmentDTO> getFollowedEquipment(Long employeeId, Pageable pageable);

    List<List<String>> getAllForExport(UserDetails user, Predicate predicate, boolean isNew);
}
