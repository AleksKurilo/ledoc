package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Employee;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final EmployeeRepository employeeRepository;

    private final CustomerRepository customerRepository;

    @GetMapping("/test")
    public Employee test(@RequestParam Long id) {
        return employeeRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
