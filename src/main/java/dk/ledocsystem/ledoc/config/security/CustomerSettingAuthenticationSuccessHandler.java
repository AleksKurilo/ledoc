package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.model.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler that sets {@link dk.ledocsystem.ledoc.model.Customer owner} of the logged in admin
 * as {@link javax.servlet.http.HttpSession} attribute "customer".
 */
@RequiredArgsConstructor
class CustomerSettingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeRepository employeeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().contains(SecurityConstants.ADMIN_AUTHORITY);
        if (isAdmin) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            Employee employee = employeeRepository.findByUsername(username).orElseThrow(IllegalStateException::new);
            request.getSession().setAttribute("customer", employee.getCustomer());
        }
    }
}
