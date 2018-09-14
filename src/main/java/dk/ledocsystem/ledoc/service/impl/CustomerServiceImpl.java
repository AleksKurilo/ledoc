package dk.ledocsystem.ledoc.service.impl;


import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.*;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Lazy}))
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final EmployeeService employeeService;

    private final TradeRepository tradeRepository;

    private final LocationService locationService;

    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    public List<Customer> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Customer> getAll(@NonNull Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Customer> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return customerRepository.findAll(predicate, pageable);
    }

    @Override
    public Optional<Customer> getById(@NonNull Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer createCustomer(@NonNull CustomerCreateDTO customerCreateDTO) {
        Customer customer = new Customer();
        BeanCopyUtils.copyProperties(customerCreateDTO, customer);

        Employee pointOfContact = resolvePointOfContact(customerCreateDTO.getPointOfContactId());
        customer.setPointOfContact(pointOfContact);

        Set<Trade> trades = resolveTrades(customerCreateDTO.getTradeIds());
        customer.setTrades(trades);

        customerRepository.save(customer);

        Employee admin = employeeService.createEmployee(customerCreateDTO.getEmployeeCreateDTO(), customer);
        employeeService.addAuthorities(admin.getId(), UserAuthorities.ADMIN);

        LocationCreateDTO locationCreateDTO = LocationCreateDTO.builder()
                .type(LocationType.ADDRESS)
                .name(customer.getName())
                .address(customerCreateDTO.getAddress())
                .build();
        Location location = locationService.createLocation(locationCreateDTO, customer, admin, true);

        admin.setPlaceOfEmployment(location);
        sendNotificationToPointOfContact(pointOfContact);

        return customer;
    }

    @Transactional
    @Override
    public Customer updateCustomer(@NonNull Long customerId, @NonNull CustomerEditDTO customerEditDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("customer.id.not.found", customerId.toString()));
        BeanCopyUtils.copyProperties(customerEditDTO, customer, false);

        Set<Long> tradeIds = customerEditDTO.getTradeIds();
        if (tradeIds != null) {
            customer.setTrades(resolveTrades(tradeIds));
        }

        Long pointOfContactId = customerEditDTO.getPointOfContactId();
        if (pointOfContactId != null) {
            Employee pointOfContact = resolvePointOfContact(pointOfContactId);
            customer.setPointOfContact(pointOfContact);
            sendNotificationToPointOfContact(pointOfContact);
        }

        return customerRepository.save(customer);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Collection<Long> customerIds) {
        customerRepository.deleteByIdIn(customerIds);
    }

    @Override
    public Customer getCurrentCustomerReference() {
        Long currentCustomerId = getCurrentUser().getCustomerId();
        return customerRepository.getOne(currentCustomerId);
    }

    private Employee resolvePointOfContact(Long pointOfContactId) {
        return (pointOfContactId == null) ? null :
                employeeService.getById(pointOfContactId)
                        .orElseThrow(() -> new NotFoundException("customer.point.of.contact.id.not.found", pointOfContactId.toString()));
    }

    private Set<Trade> resolveTrades(Set<Long> ids) {
        return new HashSet<>(tradeRepository.findAllById(ids));
    }

    private void sendNotificationToPointOfContact(Employee pointOfContact) {
        EmailNotification notification =
                new EmailNotification(pointOfContact.getUsername(), "customer_created");
        emailNotificationRepository.save(notification);
    }
}
