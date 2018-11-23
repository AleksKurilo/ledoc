package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.api.dto.inbound.LogsDTO;
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
    public LogsDTO getEmployeeLogs(@PathVariable Long employeeId, @QuerydslPredicate(root = EmployeeLog.class) Predicate predicate) {
        return  employeeLogService.getAllEmployeeLogs(employeeId, predicate);
    }

}
