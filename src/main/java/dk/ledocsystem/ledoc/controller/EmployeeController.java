package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Collection<Employee> getAllEmployees() {
        return employeeService.getAll();
    }

    @GetMapping("/{employeeId}")
    public Employee getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.getById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
    }

    @RolesAllowed("admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee createEmployee(@RequestBody @Valid EmployeeCreateDTO employeeCreateDTO) {
        return employeeService.createEmployee(employeeCreateDTO);
    }

    @PutMapping(value = "/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee updateEmployeeById(@PathVariable Long employeeId,
                                       @RequestBody @Valid EmployeeEditDTO employeeEditDTO) {
        return employeeService.updateEmployee(employeeId, employeeEditDTO);
    }

    @DeleteMapping("/{employeeId}")
    public void deleteById(@PathVariable Long employeeId) {
        employeeService.deleteById(employeeId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        employeeService.deleteByIds(ids);
    }

    @GetMapping("/role/{roleName}")
    public List<EmployeeNames> getAllByRole(@PathVariable String roleName) {
        return employeeService.getAllByRole(UserAuthorities.fromString(roleName));
    }

    @RolesAllowed({"admin", "super_admin"})
    @GetMapping(value = "/updateRoles/{employeeId}/{authorityCode}")
    public void updateAuthorities(@PathVariable Long employeeId, @PathVariable Integer authorityCode) {
        employeeService.updateAuthorities(employeeId, UserAuthorities.fromCode(authorityCode));
    }
}
