package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeSummary;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class EmployeeToSummaryPropertyMap extends PropertyMap<Employee, EmployeeSummary> {

    @Override
    protected void configure() {
        using(Converters.Collection.map(Location::getId)).map(source.getLocations(), destination.getLocationIds());
    }
}
