package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.data.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class EmployeeCreateDtoValidator extends BaseValidator<EmployeeCreateDTO> {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void validateInner(EmployeeCreateDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        String username = dto.getUsername();
        if (username != null && employeeRepository.existsByUsername(username)) {
            messages.computeIfAbsent("username", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(EMPLOYEE_USERNAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}
