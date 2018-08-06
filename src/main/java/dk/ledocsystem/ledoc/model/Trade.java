package dk.ledocsystem.ledoc.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "trades")
@ToString(of = {"name"})
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trades_seq")
    @SequenceGenerator(name = "trades_seq", sequenceName = "trades_seq")
    private Long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "trades", cascade = CascadeType.ALL)
    private Set<Customer> customer = new HashSet<>();
}
