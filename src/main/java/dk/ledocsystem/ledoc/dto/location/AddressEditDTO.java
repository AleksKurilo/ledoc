package dk.ledocsystem.ledoc.dto.location;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

@Data
public class AddressEditDTO implements AddressDTO {

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String street;

    @Size(min = 1, max = 40)
    private String buildingNumber;

    @Size(min = 4, max = 40)
    @Digits(integer = 40, fraction = 0)
    private String postalCode;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String city;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String country;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String district;
}
