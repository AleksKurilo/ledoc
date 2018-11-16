package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.EmployeeLogDTO;
import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import dk.ledocsystem.ledoc.service.EmployeeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogsController {

    private final EmployeeLogService employeeLogService;

    @GetMapping("/employee/{employeeId}")
    public EmployeeLogDTO getEmployeeLogs(@PathVariable Long employeeId, @QuerydslPredicate(root = EmployeeLog.class) Predicate predicate) {
        return  employeeLogService.getAllEmployeeLogs(employeeId, predicate);
    }

}
