package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeEditDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class EmployeeToEditDtoPropertyMap extends PropertyMap<Employee, EmployeeEditDTO> {

    @Override
    protected void configure() {
        skip(destination.getResponsible());
        map().setCustomerId(source.getCustomer().getId());
        map().setResponsibleId(source.getResponsible().getId());
        map().getDetails().setSkillResponsibleId(source.getDetails().getResponsibleOfSkills().getId());
        map().getDetails().setReviewTemplateId(source.getDetails().getReviewTemplate().getId());
        using(Converters.Collection.map(Location::getId))
                .map(source.getLocations(), destination.getLocationIds());
    }
}
