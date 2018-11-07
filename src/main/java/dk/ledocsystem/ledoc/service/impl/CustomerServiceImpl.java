package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.LocationType;
import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.CUSTOMER_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EmployeeService employeeService;
    private final TradeRepository tradeRepository;
    private final LocationService locationService;
    private final ModelMapper modelMapper;
    private final EmailNotificationRepository emailNotificationRepository;
    private final BaseValidator<CustomerCreateDTO> customerCreateDtoValidator;
    private final BaseValidator<CustomerEditDTO> customerEditDtoValidator;

    @Transactional
    @Override
    public Customer createCustomer(@NonNull CustomerCreateDTO customerCreateDTO) {
        customerCreateDtoValidator.validate(customerCreateDTO);

        Customer customer = modelMapper.map(customerCreateDTO, Customer.class);
        Employee pointOfContact = resolvePointOfContact(customerCreateDTO.getPointOfContactId());
        customer.setPointOfContact(pointOfContact);

        Set<Trade> trades = resolveTrades(customerCreateDTO.getTradeIds());
        customer.setTrades(trades);

        customer = customerRepository.save(customer);

        EmployeeCreateDTO employeeCreateDTO = customerCreateDTO.getEmployeeCreateDTO();
        employeeCreateDTO.setRole("admin");
        Employee admin = employeeService.createEmployee(employeeCreateDTO, customer);

        LocationDTO locationDTO = LocationDTO.builder()
                .type(LocationType.ADDRESS)
                .name(customer.getName())
                .address(customerCreateDTO.getAddress())
                .build();
        Location location = locationService.createLocation(locationDTO, customer, admin, true);

        admin.setPlaceOfEmployment(location);
        if (pointOfContact != null) {
            sendNotificationToPointOfContact(pointOfContact);
        }

        return customer;
    }

    @Transactional
    @Override
    public Customer updateCustomer(@NonNull CustomerEditDTO customerEditDTO) {
        customerEditDtoValidator.validate(customerEditDTO);
        Customer customer = customerRepository.findById(customerEditDTO.getId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerEditDTO.getId().toString()));
        modelMapper.map(customerEditDTO, customer);

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
    @Transactional
    public void changeArchivedStatus(@NonNull Long customerId, @NonNull Boolean archived) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("customer.id.not.found", customerId.toString()));

        customer.setArchived(archived);
        customerRepository.save(customer);
    }

    @Override
    public Long getCurrentCustomerId() {
        return getCurrentUser().getCustomerId();
    }

    @Override
    public Customer getCurrentCustomerReference() {
        return customerRepository.getOne(getCurrentCustomerId());
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

    //region GET/DELETE standard API

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> getAll(@NonNull Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(customerRepository.findAll(predicate));
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
    public List<Customer> getAllById(@NonNull Iterable<Long> ids) {
        return customerRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> customerIds) {
        customerRepository.deleteById(customerIds);
    }

    //endregion
}
