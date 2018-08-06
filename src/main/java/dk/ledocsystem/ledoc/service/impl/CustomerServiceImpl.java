package dk.ledocsystem.ledoc.service.impl;


import dk.ledocsystem.ledoc.dto.CustomerDTO;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Address;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final EmployeeRepository employeeRepository;

    private final TradeRepository tradeRepository;

    private final LocationRepository locationRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer(customerDTO);

        Employee pointOfContact = resolvePointOfContact(customerDTO.getPointOfContactId());
        customer.setPointOfContact(pointOfContact);

        Set<Trade> trades = resolveTrades(customerDTO.getTradeIds());
        customer.setTrades(trades);

        //----end of customer

        Location location = new Location(customerDTO.getLocationDTO());
        Address address = new Address(customerDTO.getLocationDTO().getAddressDTO());
        location.setAddress(address);
        address.setLocation(location);
        location.setCustomer(customer);
        Employee admin = new Employee(customerDTO.getEmployeeDTO());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        location.setResponsible(admin);

        //-----end of location

        admin.setCustomer(customer);

        locationRepository.save(location);
        return customer;
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

    private Employee resolvePointOfContact(Long pointOfContactId) {
        return employeeRepository.getOne(pointOfContactId);
    }

    private Set<Trade> resolveTrades(Set<Long> ids) {
        return tradeRepository.findAllByIdIn(ids);
    }

}
