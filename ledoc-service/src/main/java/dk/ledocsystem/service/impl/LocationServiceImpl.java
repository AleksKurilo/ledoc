package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.*;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.LocationRepository;
import dk.ledocsystem.service.api.LocationService;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.location.AddressDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.service.api.dto.outbound.location.GetLocationDTO;
import dk.ledocsystem.service.api.dto.outbound.location.LocationEditDTO;
import dk.ledocsystem.service.api.dto.outbound.location.LocationPreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.location.LocationSummary;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.impl.property_maps.location.LocationToEditDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.location.LocationToGetLocationDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.location.LocationToPhysicalLocationDtoPropertyMap;
import dk.ledocsystem.service.impl.property_maps.location.LocationToPreviewDtoPropertyMap;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class LocationServiceImpl implements LocationService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QLocation.location.customer.id, customerId);

    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final ModelMapper modelMapper;
    private final BaseValidator<LocationDTO> locationDtoValidator;

    @PostConstruct
    private void init() {
        modelMapper.addMappings(new LocationToGetLocationDtoPropertyMap());
        modelMapper.addMappings(new LocationToEditDtoPropertyMap());
        modelMapper.addMappings(new LocationToPreviewDtoPropertyMap());
        modelMapper.addMappings(new LocationToPhysicalLocationDtoPropertyMap());
    }

    @Transactional
    @Override
    public GetLocationDTO createLocation(@NonNull LocationDTO locationDTO, @NonNull UserDetails creatorDetails) {
        Long customerId = employeeRepository.findByUsername(creatorDetails.getUsername())
                .map(employee -> employee.getCustomer().getId())
                .orElseThrow(IllegalStateException::new);
        return createLocation(locationDTO, customerId, creatorDetails, false);
    }

    @Transactional
    @Override
    public GetLocationDTO createLocation(@NonNull LocationDTO locationDTO, @NonNull Long customerId,
                                         @NonNull UserDetails creatorDetails, boolean isFirstForCustomer) {
        locationDtoValidator.validate(locationDTO, ImmutableMap.of("customerId", customerId), locationDTO.getValidationGroups());

        Employee creator = employeeRepository.findByUsername(creatorDetails.getUsername()).orElseThrow(IllegalStateException::new);
        Employee responsible = resolveResponsible(locationDTO.getResponsibleId());
        Location location = (locationDTO.getType() == LocationType.ADDRESS)
                ? createAddressLocation(locationDTO)
                : createPhysicalLocation(locationDTO);

        location.setName(locationDTO.getName());
        location.setIsCustomerFirst(isFirstForCustomer);
        location.setCustomer(resolveCustomer(customerId));
        location.setResponsible(responsible);
        location.setCreatedBy(creator);
        setConnectedEmployees(location, locationDTO);

        sendMessages(responsible);
        sendMessages(creator);

        return mapToDto(locationRepository.save(location));
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
        Location owningAddressLocation = locationRepository.findById(addressLocationId)
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
    public GetLocationDTO updateLocation(@NonNull LocationDTO locationDTO, UserDetails currentUser) {
        Long customerId = employeeRepository.findByUsername(currentUser.getUsername())
                .map(Employee::getCustomer)
                .orElseThrow(IllegalStateException::new)
                .getId();
        locationDtoValidator.validate(locationDTO, ImmutableMap.of("customerId", customerId), locationDTO.getValidationGroups());

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

        return mapToDto(locationRepository.save(location));
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
            Location owningAddressLocation = locationRepository.findById(addressLocationId)
                    .orElseThrow(() -> new NotFoundException(LOCATION_ADDRESS_LOCATION_NOT_FOUND, addressLocationId.toString()));
            location.setAddressLocation(owningAddressLocation);
        }
    }

    private void updateTypeSpecificProperties(Location location, LocationDTO locationDTO) {
        if (location.getType() == LocationType.PHYSICAL) {
            Long addressLocationId = locationDTO.getAddressLocationId();
            if (!addressLocationId.equals(location.getAddressLocation().getId())) {
                Location owningAddressLocation = locationRepository.findById(addressLocationId)
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
        Location location = locationRepository.findById(locationId)
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

    private Customer resolveCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                employeeRepository.findById(responsibleId)
                        .orElseThrow(() -> new NotFoundException(EMPLOYEE_RESPONSIBLE_NOT_FOUND, responsibleId.toString()));
    }

    private Set<Employee> resolveEmployees(Collection<Long> employeeIds) {
        return new HashSet<>(employeeRepository.findAllById(employeeIds));
    }

    private void createAndSetAddress(Location owningLocation, AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        owningLocation.setAddress(address);
    }

    //region GET/DELETE standard API

    @Override
    @Transactional(readOnly = true)
    public List<GetLocationDTO> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetLocationDTO> getAll(@NonNull Pageable pageable) {
        return locationRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetLocationDTO> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetLocationDTO> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return locationRepository.findAll(predicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetLocationDTO> getAllByCustomer(@NonNull UserDetails currentUser) {
        return getAllByCustomer(currentUser, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetLocationDTO> getAllByCustomer(@NonNull UserDetails currentUser, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetLocationDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate) {
        return getAllByCustomer(currentUser, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetLocationDTO> getAllByCustomer(@NonNull UserDetails currentUser, Predicate predicate, @NonNull Pageable pageable) {
        return getAllByCustomer(currentUser, "", predicate, pageable, false, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetLocationDTO> getAllByCustomer(@NonNull UserDetails currentUser, String searchString, Predicate predicate, @NonNull Pageable pageable, boolean isNew, boolean isArchived) {
        Employee employee = employeeRepository.findByUsername(currentUser.getUsername()).orElseThrow(IllegalStateException::new);
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(employee.getCustomer().getId()));
        return locationRepository.findAll(combinePredicate, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationSummary> getAllNamesByCustomer(Long customerId) {
        Predicate notArchived = QLocation.location.archived.eq(Boolean.FALSE);
        Predicate combinePredicate = ExpressionUtils.and(notArchived, CUSTOMER_EQUALS_TO.apply(customerId));
        return locationRepository.findAll(combinePredicate).stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocationPreviewDTO> getPreviewDtoById(Long locationId) {
        return locationRepository.findById(locationId).map(this::mapToPreviewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GetLocationDTO> getById(@NonNull Long id) {
        return locationRepository.findById(id).map(this::mapToEditDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetLocationDTO> getAllById(@NonNull Iterable<Long> ids) {
        return locationRepository.findAllById(ids).stream().map(this::mapToEditDto).collect(Collectors.toList());
    }

    @Override
    public long countAllNotArchived(@NonNull Long customerId) {
        return locationRepository.countByCustomerIdAndArchivedFalse(customerId);
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

    private GetLocationDTO mapToDto(Location location) {
        return modelMapper.map(location, GetLocationDTO.class);
    }

    private LocationSummary mapToSummary(Location location) {
        return modelMapper.map(location, LocationSummary.class);
    }

    private LocationEditDTO mapToEditDto(Location location) {
        return modelMapper.map(location, LocationEditDTO.class);
    }

    private LocationPreviewDTO mapToPreviewDto(Location location) {
        return modelMapper.map(location, LocationPreviewDTO.class);
    }

    //endregion
}
