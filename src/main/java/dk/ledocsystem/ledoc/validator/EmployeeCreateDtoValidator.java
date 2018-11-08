package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_USERNAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class EmployeeCreateDtoValidator extends BaseValidator<EmployeeCreateDTO> {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void validateInner(EmployeeCreateDTO dto, Map<String, List<String>> messages) {
        if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
            messages.computeIfAbsent("username", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(EMPLOYEE_USERNAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}
