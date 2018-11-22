package dk.ledocsystem.service.api.dto.outbound.location;

import dk.ledocsystem.data.model.AddressType;
import lombok.Data;

@Data
public class GetAddressDTO {

    private AddressType addressType;

    private String street;

    private String buildingNumber;

    private String postalCode;

    private String city;

    private String country;

    private String district;
}
