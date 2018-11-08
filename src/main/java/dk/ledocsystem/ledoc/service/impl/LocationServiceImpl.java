package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.AddressDTO;
import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.dto.projections.LocationSummary;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.*;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.service.dto.GetLocationDTO;
import dk.ledocsystem.ledoc.service.dto.LocationPreviewDTO;
import dk.ledocsystem.ledoc.validator.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.*;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class LocationServiceImpl implements LocationService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QLocation.location.customer.id, customerId);

    private final ModelMapper modelMapper;
    private final LocationRepository locationRepository;
    private final EmployeeService employeeService;
    private final EmailNotificationRepository emailNotificationRepository;
    private final BaseValidator<LocationDTO> locationDtoValidator;

    @Transactional
    @Override
    public Location createLocation(@NonNull LocationDTO locationDTO, @NonNull Customer customer) {
        return createLocation(locationDTO, customer, false);
    }

    @Transactional
    @Override
    public Location createLocation(@NonNull LocationDTO locationDTO, @NonNull Customer customer, boolean isFirstForCustomer) {
        locationDtoValidator.validate(locationDTO);

        Employee creator = employeeService.getCurrentUserReference();
        Employee responsible = resolveResponsible(locationDTO.getResponsibleId());
        Location location = (locationDTO.getType() == LocationType.ADDRESS)
                ? createAddressLocation(locationDTO)
                : createPhysicalLocation(locationDTO);

        location.setName(locationDTO.getName());
        location.setIsCustomerFirst(isFirstForCustomer);
        location.setCustomer(customer);
        location.setResponsible(responsible);
        location.setCreatedBy(creator);
        setConnectedEmployees(location, locationDTO);

        sendMessages(responsible);
        sendMessages(creator);

        return locationRepository.save(location);
    }

    private Location createAddressLocation(LocationDTO locationDTO) {
        Location location = new Location();
        location.setType(LocationType.ADDRESS);
        createAndSetAddress(location, locationDTO.getAddress());
        return location;
    }

    private Location createPhysicalLocation(LocationDTO locationDTO) {
        Location location = new Location();
        location.setType(LocationType.PHYSICAL);
        Long addressLocationId = locationDTO.getAddressLocationId();
        Location owningAddressLocation = getById(addressLocationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ADDRESS_LOCATION_NOT_FOUND, addressLocationId.toString()));
        location.setAddressLocation(owningAddressLocation);

        return location;
    }

    private void setConnectedEmployees(Location location, LocationDTO locationDTO) {
        Set<Employee> employees = resolveEmployees(locationDTO.getEmployeeIds());
        employees.forEach(employee -> employee.getLocations().add(location));
        location.setEmployees(employees);
    }

    @Transactional
    @Override
    public GetLocationDTO updateLocation(@NonNull LocationDTO locationDTO) {
        locationDtoValidator.validate(locationDTO);

        Long locationId = requireNonNull(locationDTO.getId());
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));

        if (locationDTO.getType() != location.getType()) {
            changeLocationType(location, locationDTO);
        } else {
            updateTypeSpecificProperties(location, locationDTO);
        }

        location.setName(locationDTO.getName());

        changeConnectedEmployees(location, locationDTO);

        Long responsibleId = locationDTO.getResponsibleId();
        if (responsibleChanged(location.getResponsible(), responsibleId)) {
            Employee newResponsible = resolveResponsible(responsibleId);
            location.setResponsible(newResponsible);
            sendMessages(newResponsible);
        }

        return mapModelToDto(locationRepository.save(location));
    }

    private void changeLocationType(Location location, LocationDTO locationDTO) {
        if (locationDTO.getType() == LocationType.ADDRESS) {
            location.setAddressLocation(null);
            location.setType(LocationType.ADDRESS);
            createAndSetAddress(location, locationDTO.getAddress());
        } else {
            location.removeAddress();
            location.setType(LocationType.PHYSICAL);
            Long addressLocationId = locationDTO.getAddressLocationId();
            Location owningAddressLocation = getById(addressLocationId)
                    .orElseThrow(() -> new NotFoundException(LOCATION_ADDRESS_LOCATION_NOT_FOUND, addressLocationId.toString()));
            location.setAddressLocation(owningAddressLocation);
        }
    }

    private void updateTypeSpecificProperties(Location location, LocationDTO locationDTO) {
        if (location.getType() == LocationType.PHYSICAL) {
            Long addressLocationId = locationDTO.getAddressLocationId();
            if (!addressLocationId.equals(location.getAddressLocation().getId())) {
                Location owningAddressLocation = getById(addressLocationId)
                        .orElseThrow(() -> new NotFoundException(LOCATION_ADDRESS_LOCATION_NOT_FOUND, addressLocationId.toString()));
                location.setAddressLocation(owningAddressLocation);
            }
        } else {
            modelMapper.map(locationDTO.getAddress(), location.getAddress());
        }
    }

    private void changeConnectedEmployees(Location location, LocationDTO locationDTO) {
        Set<Employee> oldEmployees = location.getEmployees();
        Set<Employee> newEmployees = resolveEmployees(locationDTO.getEmployeeIds());

        oldEmployees.removeAll(newEmployees);
        oldEmployees.forEach(employee -> employee.getLocations().remove(location));
        newEmployees.forEach(employee -> employee.getLocations().add(location));

        location.setEmployees(newEmployees);
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
        if (employee != null) {
            EmailNotification notification = new EmailNotification(employee.getUsername(), "location_created");
            emailNotificationRepository.save(notification);
        }
    }

    private boolean responsibleChanged(Employee oldResponsible, Long responsibleId) {
        Long oldResponsibleId = (oldResponsible != null) ? oldResponsible.getId() : null;
        return !Objects.equals(oldResponsibleId, responsibleId);
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                employeeService.getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private Set<Employee> resolveEmployees(Collection<Long> employeeIds) {
        return new HashSet<>(employeeService.getAllById(employeeIds));
    }

    private void createAndSetAddress(Location owningLocation, AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        owningLocation.setAddress(address);
    }

    //todo It'd be better to replace this with appropriate ModelMapper configuration
    //#see ModelMapper.addMappings()
    private GetLocationDTO mapModelToDto(Location location) {
        GetLocationDTO dto = modelMapper.map(location, GetLocationDTO.class);
        if (location.getResponsible() != null) {
            dto.setResponsibleId(location.getResponsible().getId());
        }

        if (location.getAddressLocation() != null) {
            dto.setAddressLocationId(location.getAddressLocation().getId());
        }

        Set<Employee> employees = location.getEmployees();
        dto.setEmployeeIds(employees.stream().map(Employee::getId).collect(Collectors.toSet()));

        return dto;
    }

    //todo It'd be better to replace this with appropriate ModelMapper configuration
    //#see ModelMapper.addMappings()
    private LocationPreviewDTO mapModelToPreviewDto(Location location) {
        modelMapper.typeMap(Location.class, LocationPreviewDTO.class)
                .addMapping(loc -> loc.getCreatedBy().getName(), LocationPreviewDTO::setCreatedBy);

        LocationPreviewDTO dto = modelMapper.map(location, LocationPreviewDTO.class);
        if (location.getResponsible() != null) {
            dto.setResponsibleName(location.getResponsible().getName());
        }

        if (location.getAddressLocation() != null) {
            dto.setAddressLocationId(location.getAddressLocation().getId());
            dto.setAddressLocationName(location.getAddressLocation().getName());
        }

        Set<Employee> employees = location.getEmployees();
        dto.setEmployees(employees.stream().map(Employee::getName).collect(Collectors.toList()));

        return dto;
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
    public Page<LocationSummary> getAllNamesByCustomer(Long customerId, Pageable pageable) {
        return locationRepository.findAllByCustomerIdAndArchivedFalse(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetLocationDTO> getLocationDtoById(Long locationId) {
        return getById(locationId).map(this::mapModelToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocationPreviewDTO> getPreviewDtoById(Long locationId) {
        return getById(locationId).map(this::mapModelToPreviewDto);
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
