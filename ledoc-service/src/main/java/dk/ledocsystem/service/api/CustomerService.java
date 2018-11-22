package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerEditDTO;
import dk.ledocsystem.service.api.dto.outbound.GetCustomerDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface CustomerService extends DomainService<GetCustomerDTO> {

    /**
     * Creates new {@link dk.ledocsystem.data.model.Customer}, using the data from {@code customerCreateDTO}.
     *
     * @param customerCreateDTO Customer details
     * @param creatorDetails    Creator
     * @return Newly created {@link GetCustomerDTO customer}
     */
    GetCustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO, UserDetails creatorDetails);

    /**
     * Updates the properties of the customer with the given ID with properties of {@code customerCreateDTO}.
     *
     * @param customerEditDTO New properties of the customer
     * @return Updated {@link GetCustomerDTO customer}
     */
    GetCustomerDTO updateCustomer(CustomerEditDTO customerEditDTO);

    /**
     * Changes the archived status according to {@code archived}.
     */
    void changeArchivedStatus(Long customerId, Boolean archived);

    /**
     * @param username Username
     * @return Customer of the user with the given name
     */
    GetCustomerDTO getByUsername(String username);
}
