package dk.ledocsystem.ledoc.model;

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
@Table(name = "reset_tokens")
public class ResetToken {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;
}
