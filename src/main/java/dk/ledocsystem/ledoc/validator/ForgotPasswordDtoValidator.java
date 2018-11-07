package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.USER_NAME_NOT_FOUND;

@Component
@RequiredArgsConstructor
class ForgotPasswordDtoValidator extends BaseValidator<ForgotPasswordDTO> {

    private final EmployeeService employeeService;

    @Override
    protected void validateInner(ForgotPasswordDTO target, Map<String, List<String>> messages) {
        String email = target.getEmail();
        if (email != null && !employeeService.existsByUsername(target.getEmail())) {
            Locale locale = LocaleContextHolder.getLocale();
            messages.computeIfAbsent("email", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(USER_NAME_NOT_FOUND, null, locale));
        }
    }
}
