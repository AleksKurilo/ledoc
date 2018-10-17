package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDTO;
import dk.ledocsystem.ledoc.dto.review.ReviewDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.dto.EmployeePreviewDTO;
import dk.ledocsystem.ledoc.service.dto.GetEmployeeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<Employee> getAllEmployees(Pageable pageable) {
        return employeeService.getAllByCustomer(getCurrentCustomerId(), pageable);
    }

    @GetMapping("/filter")
    public Iterable<Employee> getAllFilteredEmployees(@QuerydslPredicate(root = Employee.class) Predicate predicate,
                                                      Pageable pageable) {
        return employeeService.getAllByCustomer(getCurrentCustomerId(), predicate, pageable);
    }

    @GetMapping("/new")
    public Iterable<Employee> getNewEmployeesForCurrentUser(Pageable pageable) {
        return employeeService.getNewEmployees(getCurrentUserId(), pageable);
    }

    @GetMapping("/new/filter")
    public Iterable<Employee> getNewEmployeesForCurrentUser(@QuerydslPredicate(root = Employee.class) Predicate predicate,
                                                            Pageable pageable) {
        return employeeService.getNewEmployees(getCurrentUserId(), pageable, predicate);
    }

    @GetMapping("/{employeeId}")
    public GetEmployeeDTO getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.getEmployeeDtoById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
    }

    @GetMapping("/{employeeId}/preview")
    public EmployeePreviewDTO getEmployeeByIdForPreview(@PathVariable Long employeeId) {
        return employeeService.getPreviewDtoById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
    }

    @RolesAllowed("admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee createEmployee(@RequestBody @Valid EmployeeCreateDTO employeeCreateDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return employeeService.createEmployee(employeeCreateDTO, currentCustomer);
    }

    @PutMapping(value = "/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee updateEmployeeById(@PathVariable Long employeeId,
                                       @RequestBody @Valid EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(employeeId, employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    public void deleteById(@PathVariable Long employeeId) {
        employeeService.deleteById(employeeId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        employeeService.deleteByIds(ids);
    }

    @RolesAllowed({"admin", "super_admin"})
    @PutMapping("/{employeeId}/roles")
    public void updateAuthorities(@PathVariable Long employeeId, @RequestParam String role) {
        employeeService.grantAuthorities(employeeId, UserAuthorities.fromString(role));
    }

    @PostMapping("/{employeeId}/review")
    public void performReview(@PathVariable Long employeeId, @RequestBody @Valid ReviewDTO reviewDTO) {
        employeeService.performReview(employeeId, reviewDTO);
    }

    @RolesAllowed("can_create_point_of_contact")
    @PostMapping(value = "/point-of-contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee createPointOfContact(@RequestBody @Valid EmployeeCreateDTO employeeCreateDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return employeeService.createPointOfContact(employeeCreateDTO, currentCustomer);
    }

    private Long getCurrentCustomerId() {
        return customerService.getCurrentCustomerId();
    }

    private Long getCurrentUserId() {
        return employeeService.getCurrentUser().getUserId();
    }
}
