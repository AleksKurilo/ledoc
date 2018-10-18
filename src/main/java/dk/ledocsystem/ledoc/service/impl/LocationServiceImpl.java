package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.AddressDTO;
import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.*;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class LocationServiceImpl implements LocationService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            (customerId) -> ExpressionUtils.eqConst(QLocation.location.customer.id, customerId);

    private final LocationRepository locationRepository;
    private final EmployeeService employeeService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Transactional
    @Override
    public Location createLocation(@NonNull LocationCreateDTO locationDTO, @NonNull Customer customer) {
        Long responsibleId = locationDTO.getResponsibleId();
        Employee responsible = employeeService.getById(responsibleId)
                .orElseThrow(() -> new NotFoundException("employee.responsible.not.found", responsibleId.toString()));

        return createLocation(locationDTO, customer, responsible, false);
    }

    @Transactional
    @Override
    public Location createLocation(@NonNull LocationCreateDTO locationDTO, @NonNull Customer customer,
                                   @NonNull Employee responsible, boolean isFirstForCustomer) {
        Location location = (locationDTO.getType() == LocationType.ADDRESS)
                ? createAddressLocation(locationDTO)
                : createPhysicalLocation(locationDTO);
        location.setName(locationDTO.getName());
        location.setIsCustomerFirst(isFirstForCustomer);
        location.setCustomer(customer);
        location.setResponsible(responsible);
        location.setEmployees(resolveEmployees(locationDTO.getEmployeeIds()));

        sendMessages(responsible);
        sendMessages(employeeService.getCurrentUserReference());

        return locationRepository.save(location);
    }

    private Location createAddressLocation(LocationCreateDTO locationDTO) {
        Location location = new Location();
        createAndSetAddress(location, locationDTO.getAddress());
        return location;
    }

    private Location createPhysicalLocation(LocationCreateDTO locationDTO) {
        Location location = new Location();
        Long addressLocationId = locationDTO.getAddressLocationId();
        Location owningAddressLocation = getById(addressLocationId)
                .orElseThrow(() -> new NotFoundException("location.address.location.not.found", addressLocationId.toString()));
        location.setAddressLocation(owningAddressLocation);

        return location;
    }

    @Transactional
    @Override
    public Location updateLocation(@NonNull Long locationId, @NonNull LocationEditDTO locationEditDTO) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("location.id.not.found", locationId.toString()));

        if (locationEditDTO.getType() != null) {
            changeLocationType(location, locationEditDTO);
        } else {
            updateTypeSpecificProperties(location, locationEditDTO);
        }

        if (locationEditDTO.getName() != null) {
            location.setName(locationEditDTO.getName());
        }

        Set<Long> employeeIds = locationEditDTO.getEmployeeIds();
        if (employeeIds != null) {
            Set<Employee> employees = location.getEmployees();
            employees.clear();
            employees.addAll(resolveEmployees(employeeIds));
        }

        Long responsibleId = locationEditDTO.getResponsibleId();
        if (responsibleId != null) {
            Employee responsible = employeeService.getById(responsibleId)
                    .orElseThrow(() -> new NotFoundException("employee.responsible.not.found", responsibleId.toString()));
            location.setResponsible(responsible);
        }

        return locationRepository.save(location);
    }

    private void changeLocationType(Location location, LocationEditDTO locationEditDTO) {
        if (locationEditDTO.getType() == LocationType.ADDRESS) {
            location.setAddressLocation(null);
            createAndSetAddress(location, locationEditDTO.getAddress());
        } else {
            location.removeAddress();
            Long addressLocationId = locationEditDTO.getAddressLocationId();
            Location owningAddressLocation = getById(addressLocationId)
                    .orElseThrow(() -> new NotFoundException("location.address.location.not.found", addressLocationId.toString()));
            location.setAddressLocation(owningAddressLocation);
        }
    }

    private void updateTypeSpecificProperties(Location location, LocationEditDTO locationEditDTO) {
        Long addressLocationId = locationEditDTO.getAddressLocationId();
        if (addressLocationId != null) {
            Location owningAddressLocation = getById(addressLocationId)
                    .orElseThrow(() -> new NotFoundException("location.address.location.not.found", addressLocationId.toString()));
            location.setAddressLocation(owningAddressLocation);
        }

        if (locationEditDTO.getAddress() != null) {
            BeanCopyUtils.copyProperties(locationEditDTO.getAddress(), location.getAddress(), false);
        }
    }

    @Override
    @Transactional
    public void changeArchivedStatus(@NonNull Long locationId, @NonNull ArchivedStatusDTO archivedStatusDTO) {
        Location location = getById(locationId)
                .orElseThrow(() -> new NotFoundException("location.id.not.found", locationId.toString()));

        location.setArchived(archivedStatusDTO.isArchived());
        location.setArchiveReason(archivedStatusDTO.getArchiveReason());
        locationRepository.save(location);
    }

    private void sendMessages(Employee employee) {
        EmailNotification notification = new EmailNotification(employee.getUsername(), "location_created");
        emailNotificationRepository.save(notification);
    }

    private Set<Employee> resolveEmployees(Collection<Long> employeeIds) {
        return new HashSet<>(employeeService.getAllById(employeeIds));
    }

    private void createAndSetAddress(Location owningLocation, AddressDTO addressDTO) {
        Address address = new Address();
        BeanCopyUtils.copyProperties(addressDTO, address);
        owningLocation.setAddress(address);
    }

    //region GET/DELETE standard API

    @Override
    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    @Override
    public Page<Location> getAll(@NonNull Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    @Override
    public List<Location> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(locationRepository.findAll(predicate));
    }

    @Override
    public Page<Location> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return locationRepository.findAll(predicate, pageable);
    }

    @Override
    public List<Location> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Location> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<Location> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Location> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return locationRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Location> getById(@NonNull Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public List<Location> getAllById(@NonNull Iterable<Long> ids) {
        return locationRepository.findAllById(ids);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        locationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Iterable<Long> locationIds) {
        locationRepository.deleteByIdIn(locationIds);
    }

    //endregion
}
