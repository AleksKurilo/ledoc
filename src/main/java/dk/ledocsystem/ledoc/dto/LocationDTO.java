package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LocationDTO {

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40, message = "Name must be at least 3 symbols, but not more than 40")
    private String name;

    private AddressDTO addressDTO;
}
