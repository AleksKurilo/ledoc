package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {

    @NotNull
    @Size(max = 500)
    private String street;

    @Size(min = 1, max = 40)
    private String buildingNumber;

    @NotNull
    private String postalCode;

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String city;

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String country;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String district;
}
