package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import org.modelmapper.PropertyMap;

public class EmployeeToGetEmployeeDtoPropertyMap extends PropertyMap<Employee, GetEmployeeDTO> {

    @Override
    protected void configure() {
        map().setResponsible(source.getResponsible().getName());
        map().setCustomerId(source.getCustomer().getId());
    }
}
