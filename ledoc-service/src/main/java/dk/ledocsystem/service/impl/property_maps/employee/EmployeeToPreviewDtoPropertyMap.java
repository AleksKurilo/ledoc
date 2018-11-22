package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeePreviewDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class EmployeeToPreviewDtoPropertyMap extends PropertyMap<Employee, EmployeePreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().getDetails().setSkillResponsibleName(source.getDetails().getResponsibleOfSkills().getName());
        map().getDetails().setReviewTemplateName(source.getDetails().getReviewTemplate().getName());
        using(Converters.Collection.map(Location::getName))
                .map(source.getLocations(), destination.getLocationNames());
    }
}
