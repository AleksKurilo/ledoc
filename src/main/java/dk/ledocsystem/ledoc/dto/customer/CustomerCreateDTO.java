package dk.ledocsystem.ledoc.dto.customer;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.customer.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueCVR;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueName;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.dto.LocationDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CustomerCreateDTO extends CustomerEditDTO {

    @Valid
    private LocationDTO locationDTO;

    @Valid
    private EmployeeDTO employeeDTO;
}
