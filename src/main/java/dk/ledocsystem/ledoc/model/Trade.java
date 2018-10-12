package dk.ledocsystem.ledoc.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString(of = {"nameEn"})
public class Trade {

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
}