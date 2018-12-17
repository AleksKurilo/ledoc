package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.ForgotPasswordDTO;
import dk.ledocsystem.service.api.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.USER_NAME_NOT_FOUND;

@Component
@RequiredArgsConstructor
class ForgotPasswordDtoValidator extends BaseValidator<ForgotPasswordDTO> {

    private final EmployeeService employeeService;

    @Override
    protected void validateInner(ForgotPasswordDTO target, Map<String, Object> params, Map<String, List<String>> messages) {
        String email = target.getEmail();
        if (email != null && !employeeService.existsByUsername(target.getEmail())) {
            messages.computeIfAbsent("email", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(USER_NAME_NOT_FOUND, null, getLocale()));
        }
    }
}
