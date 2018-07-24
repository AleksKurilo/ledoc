package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends IllegalStateException {

    public EmployeeNotFoundException(Long employeeId) {
        super(String.format("Employee with ID %d not found", employeeId));
    }
}
