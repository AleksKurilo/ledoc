package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.data.projections.LocationSummary;
import dk.ledocsystem.service.api.dto.outbound.location.GetLocationDTO;
import dk.ledocsystem.service.api.dto.outbound.location.LocationPreviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface LocationService extends CustomerBasedDomainService<GetLocationDTO> {

    /**
     * Creates new {@link dk.ledocsystem.data.model.Location}, using the data from {@code locationDTO}.
     *
     * @param locationDTO    Location properties
     * @param creatorDetails Creator
     * @return Newly created {@link GetLocationDTO location}
     */
    GetLocationDTO createLocation(LocationDTO locationDTO, UserDetails creatorDetails);

    /**
     * Creates new {@link dk.ledocsystem.data.model.Location}, using the data from {@code locationDTO},
     * and assigns {@code customer} to it.
     *
     * @param locationDTO        Location properties
     * @param customerId         ID of the customer - the owner of location
     * @param creatorDetails     Creator
     * @param isFirstForCustomer if {@code true}, the flag indicating that this location is first for customer
     *                           and therefore cannot be deleted will be set
     * @return Newly created {@link GetLocationDTO location}
     */
    GetLocationDTO createLocation(LocationDTO locationDTO, Long customerId, UserDetails creatorDetails, boolean isFirstForCustomer);

    /**
     * @param locationDTO Properties of the location
     * @param currentUser Current user
     * @return Updated {@link GetLocationDTO location}
     */
    GetLocationDTO updateLocation(LocationDTO locationDTO, UserDetails currentUser);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long locationId, ArchivedStatusDTO archivedStatusDTO);

    Page<LocationSummary> getAllNamesByCustomer(Long customerId, Pageable pageable);

    Optional<LocationPreviewDTO> getPreviewDtoById(Long locationId);
}
