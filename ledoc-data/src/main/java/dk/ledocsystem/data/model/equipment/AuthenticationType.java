package dk.ledocsystem.data.model.equipment;

import dk.ledocsystem.data.model.DoubleNamed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "authentication_types")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthenticationType implements DoubleNamed {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authentication_type_seq")
    @SequenceGenerator(name = "authentication_type_seq", sequenceName = "authentication_type_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameEn;

    @Column
    private String nameDa;

    @Override
    public String toString() {
        return nameEn;
    }
}
