package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import dk.ledocsystem.ledoc.service.EmployeeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogsController {

    private final EmployeeLogService employeeLogService;

    @GetMapping("/employee")
    public List<AbstractLogDTO> getEmployeeLogs(@QuerydslPredicate(root = EmployeeLog.class) Predicate predicate) {
        return  employeeLogService.getAllEmployeeLogs(predicate);
    }

}
