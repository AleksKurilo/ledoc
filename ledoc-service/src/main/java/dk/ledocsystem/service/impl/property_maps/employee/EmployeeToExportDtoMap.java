package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeExportDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeToExportDtoMap extends PropertyMap<Employee, EmployeeExportDTO> {

    @Override
    protected void configure() {
        Converter<Set<Location>, String> locationNamesConverter = context -> context.getSource() == null ? "" : StringUtils.join(context.getSource().stream().map(Location::getName).collect(Collectors.toList()), ',');
        using(locationNamesConverter).map(source.getLocations(), destination.getLocationNames());
    }
}
