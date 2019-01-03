package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsDTO {
    private long id;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;
}
