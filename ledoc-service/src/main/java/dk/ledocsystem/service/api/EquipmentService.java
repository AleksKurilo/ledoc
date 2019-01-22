package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.*;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentExportDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetFollowedEquipmentDTO;
import org.apache.poi.ss.usermodel.Workbook;
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
     * @param equipmentDTO       New properties of the equipment
     * @param currentUserDetails Current user
     * @return Updated {@link GetEquipmentDTO equipment}
     */
    GetEquipmentDTO updateEquipment(EquipmentDTO equipmentDTO, UserDetails currentUserDetails);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long equipmentId, ArchivedStatusDTO archivedStatusDTO, UserDetails creatorDetails);

    long countNewEquipment(UserDetails user);

    Optional<EquipmentPreviewDTO> getPreviewDtoById(Long equipmentId, boolean isSaveLog, UserDetails creatorDetails);

    void loanEquipment(Long equipmentId, EquipmentLoanDTO equipmentLoanDTO);

    void returnLoanedEquipment(Long equipmentId);

    List<IdAndLocalizedName> getAuthTypes();

    IdAndLocalizedName createAuthType(AuthenticationTypeDTO authenticationTypeDTO);

    List<IdAndLocalizedName> getCategories();

    IdAndLocalizedName createCategory(EquipmentCategoryCreateDTO categoryCreateDTO);

    void follow(Long equipmentId, UserDetails currentUser, EquipmentFollowDTO equipmentFollowDTO);

    Page<GetFollowedEquipmentDTO> getFollowedEquipment(Long employeeId, Pageable pageable);

    List<EquipmentExportDTO> getAllForExport(UserDetails creatorDetails, String searchString, Predicate predicate, boolean isNew);

    Workbook exportToExcel(UserDetails currentUserDetails, String searchString, Predicate predicate, boolean isNew);
}
