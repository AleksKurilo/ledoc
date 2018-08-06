package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {

    @NotNull(message = "Street can not be null")
    @Size(max = 500, message = "Street can not be more than 500 symbols")
    private String street;

    @Size(min = 1, max = 40, message = "Message can not be more than 40 symbols")
    private String buildingNumber;

    @NotNull
    private String postalCode;

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40, message = "City can not be more than 40 symbols or less than 3")
    private String city;

    @NotNull
    @OnlyAscii
    @Size(min = 3, max = 40, message = "Country can not be more than 40 symbols or less than 3")
    private String country;

    @OnlyAscii
    @Size(min = 3, max = 40, message = "District can not be more than 40 symbols or less than 3")
    private String district;
}
