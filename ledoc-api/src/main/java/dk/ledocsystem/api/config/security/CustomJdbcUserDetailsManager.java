package dk.ledocsystem.api.config.security;

import dk.ledocsystem.data.model.security.UserAuthorities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.util.List;

class CustomJdbcUserDetailsManager extends JdbcUserDetailsManager {
    private static final String USERS_BY_USERNAME_QUERY = "select username, password, NOT archived " +
            "from employees where username = ?";
    private static final String AUTHORITIES_BY_USERNAME_QUERY = "select authority " +
            "from employee_authorities inner join employees on employee_authorities.employee_id = employees.id " +
            "where username = ?";

    CustomJdbcUserDetailsManager(DataSource dataSource) {
        super(dataSource);
        setRolePrefix("ROLE_");
        setUsersByUsernameQuery(USERS_BY_USERNAME_QUERY);
        setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);
    }

    @Override
    protected List<GrantedAuthority> loadUserAuthorities(String username) {
        return getJdbcTemplate().query(getAuthoritiesByUsernameQuery(),
                new String[]{username}, (rs, rowNum) -> {
                    int authorityCode = rs.getInt("authority");
                    String authorities = UserAuthorities.fromCode(authorityCode).toString().toLowerCase();
                    String roleName = getRolePrefix() + authorities;

                    return new SimpleGrantedAuthority(roleName);
                });
    }
}
