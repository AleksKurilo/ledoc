package dk.ledocsystem.service.impl.excel.sheets.employees;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.dto.outbound.employee.EmployeeExportDTO;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.Row;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class EmployeesEntitySheet implements EntitySheet {

    private final EmployeeService employeeService;
    private UserDetails currentUserDetails;
    private Predicate predicate;
    private boolean isNew;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "TITLE", "USERNAME", "CELL_PHONE", "PHONE_NUMBER", "LOCATIONS");
    }

    @Override
    public List<Row> getRows() {
        return employeeService.getAllForExport(currentUserDetails, predicate, isNew)
                .stream()
                .map(EmployeeExportDTO::getFields)
                .map(Row::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }
}
