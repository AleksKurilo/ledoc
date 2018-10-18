package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<Location> getAllLocations(Pageable pageable) {
        return locationService.getAllByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/filter")
    public Iterable<Location> getAllFilteredLocations(@QuerydslPredicate(root = Location.class) Predicate predicate,
                                                      Pageable pageable) {
        return locationService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
    }

    @GetMapping("/{locationId}")
    public Location getLocationById(@PathVariable Long locationId) {
        return locationService.getById(locationId)
                .orElseThrow(() -> new NotFoundException("location.id.not.found", locationId.toString()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Location createLocation(@RequestBody @Valid LocationCreateDTO locationCreateDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return locationService.createLocation(locationCreateDTO, currentCustomer);
    }

    @PutMapping(value = "/{locationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Location updateLocationById(@PathVariable Long locationId,
                                       @RequestBody @Valid LocationEditDTO locationEditDTO) {
        return locationService.updateLocation(locationId, locationEditDTO);
    }

    @PostMapping("/{locationId}/archive")
    public void changeArchivedStatus(@PathVariable Long locationId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        locationService.changeArchivedStatus(locationId, archivedStatusDTO);
    }

    @DeleteMapping("/{locationId}")
    public void deleteById(@PathVariable Long locationId) {
        locationService.deleteById(locationId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        locationService.deleteByIds(ids);
    }

    private Long getCurrentCustomerId() {
        return customerService.getCurrentCustomerId();
    }
}

