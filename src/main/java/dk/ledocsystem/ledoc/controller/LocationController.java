package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.dto.projections.LocationSummary;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.service.dto.GetLocationDTO;
import dk.ledocsystem.ledoc.service.dto.LocationPreviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.LOCATION_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<Location> getAllLocations(@QuerydslPredicate(root = Location.class) Predicate predicate,
                                              Pageable pageable) {
        return locationService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
    }

    @GetMapping("/names")
    public Iterable<LocationSummary> getAllLocationNames(Pageable pageable) {
        return locationService.getAllNamesByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/{locationId}")
    public GetLocationDTO getLocationById(@PathVariable Long locationId) {
        return locationService.getLocationDtoById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    @GetMapping("/{locationId}/preview")
    public LocationPreviewDTO getLocationByIdForPreview(@PathVariable Long locationId) {
        return locationService.getPreviewDtoById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Location createLocation(@RequestBody LocationDTO locationDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        locationDTO.setCustomerId(getCurrentCustomerId());
        return locationService.createLocation(locationDTO, currentCustomer);
    }

    @PutMapping(value = "/{locationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetLocationDTO updateLocationById(@PathVariable Long locationId,
                                       @RequestBody LocationDTO locationDTO) {
        locationDTO.setId(locationId);
        locationDTO.setCustomerId(getCurrentCustomerId());
        return locationService.updateLocation(locationDTO);
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

