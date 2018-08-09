package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.model.Customer;

public interface CustomerService extends DomainService<Customer> {

    /**
     * Creates new {@link Customer}, using the data from {@code customerCreateDTO}.
     *
     * @param customerCreateDTO customerCreateDTO
     * @return Newly created {@link Customer} customer
     */
    Customer createCustomer(CustomerCreateDTO customerCreateDTO);

    /**
     * Updates the properties of the customer with the given ID with properties of {@code customerCreateDTO}.
     *
     * @param customerId  ID of the customer
     * @param customerEditDTO New properties of the customer
     * @return Updated {@link Customer}
     */
    Customer updateCustomer(Long customerId, CustomerEditDTO customerEditDTO);
}
