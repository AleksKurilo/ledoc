package dk.ledocsystem.ledoc.service.impl;


import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Address;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final EmployeeService employeeService;

    private final TradeRepository tradeRepository;

    private final LocationRepository locationRepository;

    private final PasswordEncoder passwordEncoder;

    private final SimpleMailService mailService;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

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
    public Customer createCustomer(CustomerCreateDTO customerCreateDTO) {
        Customer customer = new Customer(customerCreateDTO);

        Employee pointOfContact = resolvePointOfContact(customerCreateDTO.getPointOfContactId());
        customer.setPointOfContact(pointOfContact);

        Set<Trade> trades = resolveTrades(customerCreateDTO.getTradeIds());
        customer.setTrades(trades);

        Location location = new Location(customerCreateDTO.getLocationDTO());
        Address address = new Address(customerCreateDTO.getLocationDTO().getAddressDTO());
        location.setAddress(address);
        address.setLocation(location);
        location.setCustomer(customer);
        Employee admin = new Employee(customerCreateDTO.getEmployeeDTO());
        buildAndSendMessage(customerCreateDTO.getEmployeeDTO());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        location.setResponsible(admin);

        admin.setCustomer(customer);

        locationRepository.save(location);
        employeeService.addAuthorities(admin.getId(), UserAuthorities.ADMIN);

        if (customerCreateDTO.getEmployeeDTO().isCanCreatePersonalLocation()) {
            employeeService.addAuthorities(admin.getId(), UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }

        return customer;
    }

    @Transactional
    @Override
    public Customer updateCustomer(Long customerId, CustomerEditDTO customerEditDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(Customer.class, customerId));
        customer.updateProperties(customerEditDTO);
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
        return employeeService.getById(pointOfContactId).orElseThrow(IllegalStateException::new);
    }

    private Set<Trade> resolveTrades(Set<Long> ids) {
        return tradeRepository.findAllByIdIn(ids);
    }

    private void buildAndSendMessage(EmployeeDTO admin) {
        mailService.sendEmail(fromEmailAddress, admin.getUsername(), WelcomeEmailHolder.TOPIC, buildBody(admin));
    }

    private String buildBody(EmployeeDTO admin) {
        StringBuilder builder = new StringBuilder();
        if (admin.isWelcomeMessage()) {
            builder.append(WelcomeEmailHolder.BODY_W).append("\n\n");
        }
        builder.append(String.format(WelcomeEmailHolder.CREDENTIALS, admin.getUsername(), admin.getPassword()));
        builder.append("\n\n");
        builder.append(WelcomeEmailHolder.FOOTER);
        return builder.toString();
    }
}
