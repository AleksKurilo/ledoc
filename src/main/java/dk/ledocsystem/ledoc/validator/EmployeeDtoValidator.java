package dk.ledocsystem.ledoc.validator;


import dk.ledocsystem.ledoc.dto.employee.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_USERNAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class EmployeeDtoValidator extends BaseValidator<EmployeeDTO> {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void validateUniqueProperty(EmployeeDTO dto, Map<String, List<String>> messages) {
        Employee employee = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, dto.getId().toString()));
        String existUserName = employee.getUsername();
        String newUserName = dto.getUsername();
        if (!existUserName.equals(newUserName) && employeeRepository.existsByUsername(newUserName)) {
            messages.computeIfAbsent("userName", k -> new ArrayList<>()).add(this.messageSource.getMessage(EMPLOYEE_USERNAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}
