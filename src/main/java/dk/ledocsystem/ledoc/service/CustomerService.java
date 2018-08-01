package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.CustomerDTO;
import dk.ledocsystem.ledoc.model.Customer;

public interface CustomerService extends DomainService<Customer> {

    /**
     * Creates new {@link Customer}, using the data from {@code customerDTO}.
     *
     * @param customerDTO customerDTO
     * @return Newly created {@link Customer} customer
     */
    Customer createEmployee(CustomerDTO customerDTO);

    /**
     * Updates the properties of the customer with the given ID with properties of {@code customerDTO}.
     *
     * @param customerId  ID of the customer
     * @param customerDTO New properties of the customer
     * @return Updated {@link Customer}
     */
    Customer updateCustomer(Long customerId, CustomerDTO customerDTO);
}
