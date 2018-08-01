package dk.ledocsystem.ledoc.service.impl;


import dk.ledocsystem.ledoc.dto.CustomerDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer createEmployee(CustomerDTO customerDTO) {
        Customer customer = new Customer(customerDTO);
        return customerRepository.save(customer);
    }

    @Transactional
    @Override
    public Customer updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(Customer.class, customerId));
        customer.updateProperties(customerDTO);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(Collection<Long> customerIds) {
        customerRepository.deleteByIdIn(customerIds);
    }
}
