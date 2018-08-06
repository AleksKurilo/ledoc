package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] SWAGGER_RESOURCES = new String[] {"/swagger-ui.html", "/webjars/**",
            "/swagger-resources/**", "/v2/api-docs"};

    private final EmployeeRepository employeeRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, SWAGGER_RESOURCES).permitAll()
                    .antMatchers(HttpMethod.POST, "/password/forgot", "/password/reset").permitAll()
                    .antMatchers(HttpMethod.POST, "/customer/creteNewCustomer").hasRole("SUPER_ADMIN")
                    .anyRequest().permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(Arrays.asList(HttpHeaders.LOCATION, HttpHeaders.AUTHORIZATION));
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
