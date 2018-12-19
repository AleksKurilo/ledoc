package dk.ledocsystem.service.impl.property_maps.employee;

import dk.ledocsystem.data.model.employee.FollowedEmployees;
import dk.ledocsystem.service.api.dto.outbound.employee.GetFollowedEmployeeDTO;
import org.modelmapper.PropertyMap;

public class FollowedEmployeesToGetFollowedEmployeesDtoMap extends PropertyMap<FollowedEmployees, GetFollowedEmployeeDTO> {

    @Override
    protected void configure() {
        map().setId(source.getFollowed().getId());
        map().setName(source.getFollowed().getName());
    }
}
