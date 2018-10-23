package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.employee.Employee;

public interface LocationService extends CustomerBasedDomainService<Location> {

    /**
     * Creates new {@link Location}, using the data from {@code locationDTO}, and assigns {@code customer} to it.
     *
     * @param locationDTO Location properties
     * @param customer    Customer - the owner of location
     * @return Newly created {@link Location}
     */
    Location createLocation(LocationCreateDTO locationDTO, Customer customer);

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
    Location createLocation(LocationCreateDTO locationDTO, Customer customer, Employee responsible, boolean isFirstForCustomer);

    /**
     * Updates the properties of the location with the given ID with properties of {@code locationEditDTO}.
     *
     * @param locationId      ID of the location
     * @param locationEditDTO New properties of the location
     * @return Updated {@link Location}
     */
    Location updateLocation(Long locationId, LocationEditDTO locationEditDTO);

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long locationId, ArchivedStatusDTO archivedStatusDTO);
}
