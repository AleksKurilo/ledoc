package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.service.EmployeeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogsController {

    private final EmployeeLogService employeeLogService;

    @GetMapping("/employee/{employeeId}")
    public List<AbstractLogDTO> getEmployeeLogs(@PathVariable Long employeeId) {
        return  employeeLogService.getAllEmployeeLogs(employeeId);
    }

}
