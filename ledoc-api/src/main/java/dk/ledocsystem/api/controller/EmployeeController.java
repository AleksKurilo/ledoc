package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.ChangePasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.review.ReviewDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.CustomerService;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeePreviewDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeSummaryDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CustomerService customerService;

    @GetMapping
    public Iterable<GetEmployeeDTO> getAllEmployees(@CurrentUser UserDetails currentUser, Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return employeeService.getAllByCustomer(customerId, pageable);
    }

    @GetMapping("/filter")
    public Iterable<GetEmployeeDTO> getAllFilteredEmployees(@CurrentUser UserDetails currentUser,
                                                            @QuerydslPredicate(root = Employee.class) Predicate predicate,
                                                            @PageableDefault(sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        Long customerId = getCustomerId(currentUser);
        return employeeService.getAllByCustomer(customerId, predicate, pageable);
    }

    @GetMapping("/names")
    public Iterable<EmployeeSummaryDTO> getAllEmployeeNames(@CurrentUser UserDetails currentUser) {
        Long customerId = getCustomerId(currentUser);
        return new PageImpl<>(employeeService.getAllNamesByCustomer(customerId));
    }

    @GetMapping("/new")
    public Iterable<GetEmployeeDTO> getNewEmployeesForCurrentUser(@CurrentUser UserDetails currentUser, Pageable pageable) {
        return employeeService.getNewEmployees(currentUser, pageable);
    }

    @GetMapping("/new/filter")
    public Iterable<GetEmployeeDTO> getNewEmployeesForCurrentUser(@CurrentUser UserDetails currentUser,
                                                                  @QuerydslPredicate(root = Employee.class) Predicate predicate,
                                                                  @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        return employeeService.getNewEmployees(currentUser, pageable, predicate);
    }

    @GetMapping("/{employeeId}")
    public GetEmployeeDTO getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.getById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
    }

    @GetMapping("/{employeeId}/preview")
    public EmployeePreviewDTO getEmployeeByIdForPreview(@PathVariable Long employeeId,
                                                        @RequestParam(value = "savelog", required = false) boolean isSaveLog,
                                                        @CurrentUser UserDetails currentUser) {
        return employeeService.getPreviewDtoById(employeeId, isSaveLog, currentUser)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
    }

    @RolesAllowed("admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetEmployeeDTO createEmployee(@RequestBody EmployeeCreateDTO employeeCreateDTO, @CurrentUser UserDetails currentUser) {
        return employeeService.createEmployee(employeeCreateDTO, currentUser);
    }

    @PutMapping(value = "/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetEmployeeDTO updateEmployeeById(@PathVariable Long employeeId,
                                             @RequestBody EmployeeDTO employeeDTO,
                                             @CurrentUser UserDetails currentUser) {
        employeeDTO.setId(employeeId);
        return employeeService.updateEmployee(employeeDTO, currentUser);
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
    public void performReview(@PathVariable Long employeeId, @RequestBody ReviewDTO reviewDTO,
                              @CurrentUser UserDetails currentUser) {
        employeeService.performReview(employeeId, reviewDTO, currentUser);
    }

    @PostMapping("/{employeeId}/archive")
    public void changeArchivedStatus(@PathVariable Long employeeId, @RequestBody ArchivedStatusDTO archivedStatusDTO,
                                     @CurrentUser UserDetails currentUser) {
        employeeService.changeArchivedStatus(employeeId, archivedStatusDTO, currentUser);
    }

    @PostMapping("/{employeeId}/password/change")
    public void changePassword(@PathVariable Long employeeId, @RequestBody ChangePasswordDTO changePasswordDTO) {
        employeeService.changePassword(employeeId, changePasswordDTO);
    }

    @RolesAllowed("can_create_point_of_contact")
    @PostMapping(value = "/point-of-contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetEmployeeDTO createPointOfContact(@RequestBody EmployeeCreateDTO employeeCreateDTO, @CurrentUser UserDetails currentUser) {
        return employeeService.createPointOfContact(employeeCreateDTO, currentUser);
    }

    private Long getCustomerId(UserDetails user) {
        return customerService.getByUsername(user.getUsername()).getId();
    }
}
