package dk.ledocsystem.ledoc.service;


import dk.ledocsystem.ledoc.dto.CustomerDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Creates new {@link Customer}, using the data from {@code customerDTO}
     *
     * @param customerDTO customerDTO
     * @return Newly created {@link Customer} customer
     */
    public Customer createEmployee(CustomerDTO customerDTO) {
        Customer customer = new Customer(customerDTO);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(Customer.class, customerId));
        customer.updateProperties(customerDTO);
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteByIds(Collection<Long> customerIds) {
        customerRepository.deleteByIdIn(customerIds);
    }
}
