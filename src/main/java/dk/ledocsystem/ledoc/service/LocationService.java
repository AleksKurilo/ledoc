package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.dto.projections.LocationSummary;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.service.dto.GetLocationDTO;
import dk.ledocsystem.ledoc.service.dto.LocationPreviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LocationService extends CustomerBasedDomainService<Location> {

    /**
     * Creates new {@link Location}, using the data from {@code locationDTO}, and assigns {@code customer} to it.
     *
     * @param locationDTO Location properties
     * @param customer    Customer - the owner of location
     * @return Newly created {@link Location}
     */
    Location createLocation(LocationDTO locationDTO, Customer customer);

    /**
     * Creates new {@link Location}, using the data from {@code locationDTO},
     * and assigns {@code customer} and {@code responsible} to it.
     *
     * @param locationDTO        Location properties
     * @param customer           Customer - the owner of location
     * @param responsible        Employee - responsible for the location
     * @param isFirstForCustomer if {@code true}, the flag indicating that this location is first for customer
     *                           and therefore cannot be deleted will be set
     * @return Newly created {@link Location}
     */
    Location createLocation(LocationDTO locationDTO, Customer customer, Employee responsible, boolean isFirstForCustomer);

    /**
     * @param locationDTO Properties of the location
     * @return Updated {@link Location}
     */
    GetLocationDTO updateLocation(LocationDTO locationDTO);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long locationId, ArchivedStatusDTO archivedStatusDTO);

    Page<LocationSummary> getAllNamesByCustomer(Long customerId, Pageable pageable);

    Optional<GetLocationDTO> getLocationDtoById(Long locationId);

    Optional<LocationPreviewDTO> getPreviewDtoById(Long locationId);
}
