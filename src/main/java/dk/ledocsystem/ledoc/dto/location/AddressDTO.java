package dk.ledocsystem.ledoc.dto.location;

public interface AddressDTO {

    String getStreet();

    void setStreet(String street);

    String getBuildingNumber();

    void setBuildingNumber(String buildingNumber);

    String getPostalCode();

    void setPostalCode(String postalCode);

    String getCity();

    void setCity(String city);

    String getCountry();

    void setCountry(String country);

    String getDistrict();

    void setDistrict(String district);
}
