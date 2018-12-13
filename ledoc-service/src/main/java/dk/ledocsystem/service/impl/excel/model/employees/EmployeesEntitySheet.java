package dk.ledocsystem.service.impl.excel.model.employees;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

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
    public List<List<String>> getRows() {
        return employeeService.getAllForExport(currentUserDetails, predicate, isNew);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
