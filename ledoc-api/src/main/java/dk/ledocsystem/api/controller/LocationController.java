package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.data.projections.LocationSummary;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.LocationService;
import dk.ledocsystem.service.api.dto.outbound.location.GetLocationDTO;
import dk.ledocsystem.service.api.dto.outbound.location.LocationPreviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.LOCATION_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<GetLocationDTO> getAllLocations(@CurrentUser UserDetails currentUser,
                                                    @QuerydslPredicate(root = Location.class) Predicate predicate,
                                                    @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return locationService.getAllByCustomer(customerId, predicate, pageable);
    }

    @GetMapping("/names")
    public Iterable<LocationSummary> getAllLocationNames(@CurrentUser UserDetails currentUser,
                                                         Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return locationService.getAllNamesByCustomer(customerId, pageable);
    }

    @GetMapping("/{locationId}")
    public GetLocationDTO getLocationById(@PathVariable Long locationId) {
        return locationService.getById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    @GetMapping("/{locationId}/preview")
    public LocationPreviewDTO getLocationByIdForPreview(@PathVariable Long locationId) {
        return locationService.getPreviewDtoById(locationId)
                .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, locationId.toString()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetLocationDTO createLocation(@RequestBody LocationDTO locationDTO, @CurrentUser UserDetails currentUser) {
        return locationService.createLocation(locationDTO, currentUser);
    }

    @PutMapping(value = "/{locationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetLocationDTO updateLocationById(@PathVariable Long locationId,
                                             @RequestBody LocationDTO locationDTO,
                                             @CurrentUser UserDetails currentUser) {
        locationDTO.setId(locationId);
        return locationService.updateLocation(locationDTO, currentUser);
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

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}

