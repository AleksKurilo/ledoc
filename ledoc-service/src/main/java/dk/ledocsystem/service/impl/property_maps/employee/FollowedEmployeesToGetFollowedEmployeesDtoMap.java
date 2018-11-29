package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.employee.FollowedEmployees;
import dk.ledocsystem.service.api.dto.outbound.employee.GetFollowedEmployeeDTO;
import org.modelmapper.PropertyMap;

public class FollowedEmployeesToGetFollowedEmployeesDtoMap extends PropertyMap<FollowedEmployees, GetFollowedEmployeeDTO> {

    @Override
    protected void configure() {
        map().setId(source.getFollowedEmployee().getId());
        map().setName(source.getFollowedEmployee().getName());
        map().setForced(source.isForced());
    }
}
