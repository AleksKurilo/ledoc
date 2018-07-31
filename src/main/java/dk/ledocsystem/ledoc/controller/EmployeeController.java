package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    @GetMapping
    public Collection<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{employeeId}")
    public Employee getEmployeeById(@PathVariable Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new NotFoundException(Employee.class, employeeId));
    }

    @RolesAllowed("admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee createEmployee(@RequestBody @Valid EmployeeDTO employeeDTO,
                                   @SessionAttribute @ApiIgnore Customer customer) {
        return employeeService.createEmployee(employeeDTO, customer);
    }

    @PutMapping(value = "/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee updateEmployeeById(@PathVariable Long employeeId, @RequestBody @Valid EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(employeeId, employeeDTO);
    }

    @DeleteMapping("/{employeeId}")
    public void deleteById(@PathVariable Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        employeeService.deleteByIds(ids);
    }
}
