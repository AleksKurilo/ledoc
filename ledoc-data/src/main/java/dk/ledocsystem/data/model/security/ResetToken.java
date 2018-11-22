package dk.ledocsystem.data.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "reset_tokens")
public class ResetToken {

    @Id
    private String username;

    @Column(nullable = false, unique = true, length = 36)
    private String token;
}
