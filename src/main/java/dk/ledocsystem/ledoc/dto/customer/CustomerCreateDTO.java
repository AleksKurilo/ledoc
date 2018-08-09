package dk.ledocsystem.ledoc.dto.customer;

import dk.ledocsystem.ledoc.dto.AddressDTO;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CustomerCreateDTO extends CustomerEditDTO {

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private EmployeeDTO employeeDTO;
}
