package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public Iterable<Location> getAllLocations(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "0") Integer size) {
        if (page >= 0 && size > 0) {
            return locationService.getAll(PageRequest.of(page, size));
        }
        return locationService.getAll();
    }

    @GetMapping("/{locationId}")
    public Location getLocationById(@PathVariable Long locationId) {
        return locationService.getById(locationId)
                .orElseThrow(() -> new NotFoundException("location.id.not.found", locationId.toString()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Location createLocation(@RequestBody @Valid LocationCreateDTO locationCreateDTO) {
        return locationService.createLocation(locationCreateDTO);
    }

    @PutMapping(value = "/{locationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Location updateLocationById(@PathVariable Long locationId,
                                       @RequestBody @Valid LocationEditDTO locationEditDTO) {
        return locationService.updateLocation(locationId, locationEditDTO);
    }

    @DeleteMapping("/{locationId}")
    public void deleteById(@PathVariable Long locationId) {
        locationService.deleteById(locationId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        locationService.deleteByIds(ids);
    }
}

