package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.*;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.review.Module;
import dk.ledocsystem.data.repository.*;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.api.LocationService;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerAdminDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerEditDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.FullCustomerExportDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.GetCustomerDTO;
import dk.ledocsystem.service.api.dto.outbound.customer.ShortCustomerExportDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.customers.FullCustomersEntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.customers.ShortCustomersEntitySheet;
import dk.ledocsystem.service.impl.property_maps.customers.CustomerToFullExportDtoMap;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.CUSTOMER_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {
    private static final Function<Boolean, Predicate> CUSTOMERS_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QCustomer.customer.archived, archived);

    private final CustomerRepository customerRepository;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final TradeRepository tradeRepository;
    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final DocumentRepository documentRepository;
    private final EquipmentRepository equipmentRepository;
    private final SupplierRepository supplierRepository;
    private final ReviewTemplateRepository reviewTemplateRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final ExcelExportService excelExportService;
    private final ModelMapper modelMapper;
    private final BaseValidator<CustomerCreateDTO> customerCreateDtoValidator;
    private final BaseValidator<CustomerEditDTO> customerEditDtoValidator;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new CustomerToFullExportDtoMap());
    }

    @Transactional
    @Override
    public GetCustomerDTO createCustomer(@NonNull CustomerCreateDTO customerCreateDTO, @NonNull UserDetails creatorDetails) {
        customerCreateDTO.getAddress().setAddressType(AddressType.HEAD_OFFICE);
        customerCreateDtoValidator.validate(customerCreateDTO, customerCreateDTO.getValidationGroups());

        Customer customer = modelMapper.map(customerCreateDTO, Customer.class);
        Employee pointOfContact = resolvePointOfContact(customerCreateDTO.getPointOfContactId());
        customer.setPointOfContact(pointOfContact);

        Set<Trade> trades = resolveTrades(customerCreateDTO.getTradeIds());
        customer.setTrades(trades);

        customer = customerRepository.save(customer);

        LocationDTO locationDTO = LocationDTO.builder()
                .type(LocationType.ADDRESS)
                .name(customer.getName())
                .address(customerCreateDTO.getAddress())
                .build();
        Long locationId = locationService.createLocation(locationDTO, customer.getId(), creatorDetails, true).getId();

        CustomerAdminDTO adminDTO = customerCreateDTO.getAdmin();
        EmployeeCreateDTO employeeCreateDTO = modelMapper.map(adminDTO, EmployeeCreateDTO.class);
        employeeCreateDTO.setLocationIds(Collections.singleton(locationId));
        Long adminId = employeeService.createEmployee(employeeCreateDTO, customer.getId(), creatorDetails).getId();

        employeeRepository.findById(adminId).ifPresent(admin -> {
            Location location = locationRepository.findById(locationId).orElseThrow(IllegalStateException::new);
            admin.setPlaceOfEmployment(location);
            admin.getLocations().add(location);
            location.setResponsible(admin);
        });
        if (pointOfContact != null) {
            sendNotificationToPointOfContact(pointOfContact);
        }

        return mapToDto(customer);
    }

    @Transactional
    @Override
    public GetCustomerDTO updateCustomer(@NonNull CustomerEditDTO customerEditDTO) {
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

        return mapToDto(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long customerId, @NonNull Boolean archived) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerId.toString()));

        customer.setArchived(archived);
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public GetCustomerDTO getByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .map(Employee::getCustomer)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, username));
    }

    @Override
    public List<ShortCustomerExportDTO> getAllForExportShort(Predicate predicate) {
        return getAll(predicate).stream().map(this::mapToShortExportDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FullCustomerExportDTO> getAllForExportFull(Predicate predicate) {
        return IterableUtils.toList(customerRepository.findAll(predicate))
                .stream()
                .map(this::mapToFullExportDto)
                .peek(this::setMainLocationAddress)
                .peek(this::setExportCounters)
                .collect(Collectors.toList());
    }

    private void setMainLocationAddress(FullCustomerExportDTO fullCustomerExportDTO) {
        locationRepository.getByCustomerIdAndIsCustomerFirstTrue(fullCustomerExportDTO.getId())
                .ifPresent(location -> modelMapper.map(location.getAddress(), fullCustomerExportDTO.getAddress()));
    }

    private void setExportCounters(FullCustomerExportDTO fullCustomerExportDTO) {
        Long customerId = fullCustomerExportDTO.getId();
        fullCustomerExportDTO.setCountOfActiveSuppliers(supplierRepository.countByCustomerIdAndArchivedFalse(customerId));
        fullCustomerExportDTO.setCountOfAllSuppliers(supplierRepository.countByCustomerId(customerId));
        fullCustomerExportDTO.setCountOfActiveEmployees(employeeRepository.countByCustomerIdAndArchivedFalse(customerId));
        fullCustomerExportDTO.setCountOfAllEmployees(employeeRepository.countByCustomerId(customerId));
        fullCustomerExportDTO.setCountOfActiveDocuments(documentRepository.countByCustomerIdAndArchivedFalse(customerId));
        fullCustomerExportDTO.setCountOfAllDocuments(documentRepository.countByCustomerId(customerId));
        fullCustomerExportDTO.setCountOfActiveEquipment(equipmentRepository.countByCustomerIdAndArchivedFalse(customerId));
        fullCustomerExportDTO.setCountOfAllEquipment(equipmentRepository.countByCustomerId(customerId));
        fullCustomerExportDTO.setCountOfReviewTemplates(reviewTemplateRepository.countByCustomerId(customerId));
        fullCustomerExportDTO.setCountOfEmployeeReviewTemplates(reviewTemplateRepository.countByCustomerIdAndModule(customerId, Module.EMPLOYEES));
        fullCustomerExportDTO.setCountOfLocations(locationRepository.countByCustomerId(customerId));
    }

    @Override
    public Workbook exportToExcelShort(Predicate predicate, boolean isArchived) {
        List<EntitySheet> customerSheets = new ArrayList<>();
        Predicate predicateForCustomers = ExpressionUtils.and(predicate, CUSTOMERS_ARCHIVED.apply(false));
        customerSheets.add(new ShortCustomersEntitySheet(this, predicateForCustomers,"Customers"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, CUSTOMERS_ARCHIVED.apply(true));
            customerSheets.add(new ShortCustomersEntitySheet(this, predicateForArchived,"Archived"));
        }
        return excelExportService.exportSheets(customerSheets);
    }

    @Override
    @Transactional(readOnly = true)
    public Workbook exportToExcelFull(Predicate predicate, boolean isArchived) {
        List<EntitySheet> customerSheets = new ArrayList<>();
        Predicate predicateForCustomers = ExpressionUtils.and(predicate, CUSTOMERS_ARCHIVED.apply(false));
        customerSheets.add(new FullCustomersEntitySheet(this, predicateForCustomers,"Customers"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, CUSTOMERS_ARCHIVED.apply(true));
            customerSheets.add(new FullCustomersEntitySheet(this, predicateForArchived,"Archived"));
        }
        return excelExportService.exportSheets(customerSheets);
    }

    private Employee resolvePointOfContact(Long pointOfContactId) {
        return (pointOfContactId == null) ? null :
                employeeRepository.findById(pointOfContactId)
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
    public List<GetCustomerDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetCustomerDTO> getAll(@NonNull Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public List<GetCustomerDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<GetCustomerDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return customerRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    public Optional<GetCustomerDTO> getById(@NonNull Long id) {
        return customerRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public List<GetCustomerDTO> getAllById(@NonNull Iterable<Long> ids) {
        return customerRepository.findAllById(ids).stream().map(this::mapToDto).collect(Collectors.toList());
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

    private GetCustomerDTO mapToDto(Customer customer) {
        return modelMapper.map(customer, GetCustomerDTO.class);
    }

    private ShortCustomerExportDTO mapToShortExportDto(GetCustomerDTO customer) {
        return modelMapper.map(customer, ShortCustomerExportDTO.class);
    }

    private FullCustomerExportDTO mapToFullExportDto(Customer customer) {
        return modelMapper.map(customer, FullCustomerExportDTO.class);
    }

    //endregion
}
