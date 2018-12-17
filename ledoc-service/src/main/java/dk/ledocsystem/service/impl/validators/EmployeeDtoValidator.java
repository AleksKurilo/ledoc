package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class EmployeeDtoValidator extends BaseValidator<EmployeeDTO> {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void validateInner(EmployeeDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        Employee employee = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, dto.getId().toString()));
        String existUserName = employee.getUsername();
        String newUserName = dto.getUsername();
        if (newUserName != null && !existUserName.equals(newUserName) && employeeRepository.existsByUsername(newUserName)) {
            messages.computeIfAbsent("username", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(EMPLOYEE_USERNAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}
