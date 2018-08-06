package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.dto.AddressDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "addresses")
public class Address {

    public Address(AddressDTO addressDTO) {
        setStreet(addressDTO.getStreet());
        setBuildingNumber(addressDTO.getBuildingNumber());
        setPostalCode(addressDTO.getPostalCode());
        setCity(addressDTO.getCity());
        setCountry(addressDTO.getCountry());
        setDistrict(addressDTO.getDistrict());
    }

    @EqualsAndHashCode.Include
    @Id
    private Long id;

    @Column(nullable = false, length = 500)
    private String street;

    @Column(name = "building_number", length = 40)
    private String buildingNumber;

    @Column(nullable = false, length = 40)
    private String postalCode;

    @Column(nullable = false, length = 40)
    private String city;

    @Column(nullable = false, length = 40)
    private String country;

    @Column(length = 40)
    private String district;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location location;
}
