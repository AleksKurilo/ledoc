package dk.ledocsystem.ledoc.model.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "reset_tokens")
public class ResetToken {

    @Id
    private String username;

    @Column(nullable = false, unique = true, length = 36)
    private String token;
}
