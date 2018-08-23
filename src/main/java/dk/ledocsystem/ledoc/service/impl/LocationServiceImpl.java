package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.location.AddressDTO;
import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Address;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.LocationType;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    @Override
    public List<Location> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Location> getAll(Pageable pageable) {
        Long currentCustomerId = customerService.getCurrentCustomerReference().getId();
        return locationRepository.findAllByCustomerId(currentCustomerId, pageable);
    }

    @Override
    public Optional<Location> getById(Long id) {
        return locationRepository.findById(id);
    }

    @Transactional
    @Override
    public Location createLocation(LocationCreateDTO locationDTO) {
        return createLocation(locationDTO, customerService.getCurrentCustomerReference());
    }

    @Transactional
    @Override
    public Location createLocation(LocationCreateDTO locationDTO, Customer customer) {
        Long responsibleId = locationDTO.getResponsibleId();
        Employee responsible = employeeService.getById(responsibleId)
                .orElseThrow(() -> new NotFoundException("location.responsible.not.found", responsibleId.toString()));

        return createLocation(locationDTO, customer, responsible, false);
    }

    @Transactional
    @Override
    public Location createLocation(LocationCreateDTO locationDTO, Customer customer, Employee responsible,
                                   boolean isFirstForCustomer) {
        Location location = (locationDTO.getType() == LocationType.ADDRESS)
                ? createAddressLocation(locationDTO)
                : createPhysicalLocation(locationDTO);
        location.setName(locationDTO.getName());
        location.setIsCustomerFirst(isFirstForCustomer);
        location.setCustomer(customer);
        location.setResponsible(responsible);
        location.setEmployees(resolveEmployees(locationDTO.getEmployeeIds()));

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
    public Location updateLocation(Long locationId, LocationEditDTO locationEditDTO) {
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
                    .orElseThrow(() -> new NotFoundException("location.responsible.not.found", responsibleId.toString()));
            location.setResponsible(responsible);
        }

        return locationRepository.save(location);
    }

    private void changeLocationType(Location location, LocationEditDTO locationEditDTO) {
        if (locationEditDTO.getType() == LocationType.ADDRESS) {
            location.setAddressLocation(null);
            createAndSetAddress(location, locationEditDTO.getAddress());
        } else {
            location.setAddress(null);
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

    private Set<Employee> resolveEmployees(Collection<Long> employeeIds) {
        return new HashSet<>(employeeService.findAllById(employeeIds));
    }

    private void createAndSetAddress(Location owningLocation, AddressDTO addressDTO) {
        Address address = new Address();
        BeanCopyUtils.copyProperties(addressDTO, address);
        owningLocation.setAddress(address);
        address.setLocation(owningLocation);
    }

    @Override
    public void deleteById(Long id) {
        locationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(Collection<Long> ids) {
        locationRepository.deleteByIdIn(ids);
    }
}
