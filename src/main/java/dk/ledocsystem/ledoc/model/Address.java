package dk.ledocsystem.ledoc.model;

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

    @Column(nullable = false, length = 500)
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location location;
}
