package dk.ledocsystem.ledoc.dto.location;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.model.AddressType;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String street;

    @Size(min = 1, max = 40)
    private String buildingNumber;

    @NotNull
    @Size(min = 4, max = 40)
    @Digits(integer = 40, fraction = 0)
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

    @NotNull
    private AddressType addressType;
}
