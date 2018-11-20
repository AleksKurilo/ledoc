package dk.ledocsystem.ledoc.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;

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
    private static final String[] SWAGGER_RESOURCES = new String[] {"/swagger-ui.html", "/webjars/**",
            "/swagger-resources/**", "/v2/api-docs"};

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final JwtSettingAuthenticationSuccessHandler jwtSettingAuthenticationSuccessHandler;
    private final DataSource dataSource;
    private final JwtTokenService tokenRegistry;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, SWAGGER_RESOURCES).permitAll()
                    .antMatchers(HttpMethod.POST, "/password/forgot", "/password/reset").permitAll()
                    .anyRequest().fullyAuthenticated()
                    .and()
                .formLogin()
                    .permitAll()
                    .and()
                .logout().disable()
                .addFilter(usernamePasswordAuthenticationFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), tokenRegistry))
                .exceptionHandling()
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                    .and()
                .csrf()
                    .disable()
                .cors()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
        AuthenticationFailureHandler customAuthenticationFailureHandler =
                new CustomAuthenticationFailureHandler(messageSource, localeResolver);

        UsernamePasswordAuthenticationFilter authenticationFilter =
                new CustomUsernamePasswordAuthenticationFilter(authenticationManager());

        authenticationFilter.setAuthenticationSuccessHandler(jwtSettingAuthenticationSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        return authenticationFilter;
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
        impl.setUserDetailsService(userDetailsService());
        impl.setPasswordEncoder(passwordEncoder());
        impl.setHideUserNotFoundExceptions(false);
        return impl;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomJdbcUserDetailsManager(dataSource);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(Arrays.asList(HttpHeaders.LOCATION, HttpHeaders.AUTHORIZATION));
        configuration.setAllowedOrigins(Arrays.asList("http://testledocsystem.chisw.us",
                "http://192.168.2.218", "http://localhost:3000", "http://192.168.2.226", "http://dev-ledoc.chisw.us"));
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
