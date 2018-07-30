package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String USERS_BY_USERNAME_QUERY = "select username, password, NOT archived " +
            "from employees where username = ?";
    private static final String AUTHORITIES_BY_USERNAME_QUERY = "select username, authorities.name " +
            "from authorities inner join employees_authorities on authorities.id = employees_authorities.authority_id " +
            "inner join employees on employees_authorities.employee_id = employees.id where username = ?";

    private final DataSource dataSource;
    private final EmployeeRepository employeeRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .addFilter(usernamePasswordAuthenticationFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .csrf()
                    .disable()
                .cors()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
        AuthenticationSuccessHandler jwtSettingAuthenticationSuccessHandler =
                new JwtSettingAuthenticationSuccessHandler();
        AuthenticationSuccessHandler customerSettingAuthenticationSuccessHandler =
                new CustomerSettingAuthenticationSuccessHandler(employeeRepository);

        UsernamePasswordAuthenticationFilter authenticationFilter =
                new CustomUsernamePasswordAuthenticationFilter(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(new CompositeAuthenticationSuccessHandler(
                Arrays.asList(jwtSettingAuthenticationSuccessHandler, customerSettingAuthenticationSuccessHandler)));
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                    .dataSource(dataSource)
                    .rolePrefix("ROLE_")
                    .usersByUsernameQuery(USERS_BY_USERNAME_QUERY)
                    .authoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY)
                    .passwordEncoder(passwordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
        configuration.addExposedHeader(HttpHeaders.LOCATION);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder("", 1000, 160);
    }
}
