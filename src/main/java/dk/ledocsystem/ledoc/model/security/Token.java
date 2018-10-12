package dk.ledocsystem.ledoc.model.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_tokens")
@Getter
@Setter
@ToString(of = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_tokens_seq")
    @SequenceGenerator(name = "access_tokens_seq", sequenceName = "access_tokens_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 4000)
    private String token;

    @Column(length = 4000)
    private String newToken;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private State state = State.ONLINE;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}