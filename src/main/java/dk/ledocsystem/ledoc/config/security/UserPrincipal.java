package dk.ledocsystem.ledoc.config.security;

import lombok.Value;

import java.security.Principal;

/**
 * {@link Principal} implementation that stores user as well as its {@link dk.ledocsystem.ledoc.model.Customer} id.
 */
@Value
public class UserPrincipal implements Principal {

    String username;
    Long customerId;

    @Override
    public String getName() {
        return username;
    }
}
