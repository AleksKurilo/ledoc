package dk.ledocsystem.data.model;

import dk.ledocsystem.data.model.document.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "trades")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Trade implements DoubleNamed {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trades_seq")
    @SequenceGenerator(name = "trades_seq", sequenceName = "trades_seq")
    private Long id;

    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;

    @Column(name = "name_da", nullable = false)
    private String nameDa;

    @ManyToMany(mappedBy = "trades")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Customer> customer = new HashSet<>();

    @ManyToMany(mappedBy = "trades")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Document> documents;

    @Override
    public String toString() {
        return nameEn;
    }
}