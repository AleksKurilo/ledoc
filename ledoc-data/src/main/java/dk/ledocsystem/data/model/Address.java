package dk.ledocsystem.data.model;

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

    @EqualsAndHashCode.Include
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Column(nullable = false, length = 40)
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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location location;
}
