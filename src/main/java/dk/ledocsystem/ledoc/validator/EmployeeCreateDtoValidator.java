package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_USERNAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
public class EmployeeCreateDtoValidator extends BaseValidator<EmployeeCreateDTO> {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void validateUniqueProperty(EmployeeCreateDTO dto, Map<String, List<String>> messages) {
        if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
            messages.computeIfAbsent("userName",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(EMPLOYEE_USERNAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}
