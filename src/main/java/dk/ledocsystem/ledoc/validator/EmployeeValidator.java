package dk.ledocsystem.ledoc.validator;


import dk.ledocsystem.ledoc.dto.employee.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.ValidationDtoException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_USERNAME_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EmployeeValidator<K extends EmployeeDTO> extends BaseValidator<K> {

    private final EmployeeRepository employeeRepository;

    @Override
    public void validate(K dto) {
        Map<String, List<String>> errors = getBasicValidation(dto);
        if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
            errors.computeIfAbsent("userName", k -> new ArrayList<>()).add(this.messageSource.getMessage(EMPLOYEE_USERNAME_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
        if (!errors.isEmpty()) {
            throw new ValidationDtoException(errors);
        }
    }

    public void validateWhenUpdate(Employee employeeExist, K employeeDTO) {
        Map<String, List<String>> errors = getBasicValidation(employeeDTO);
        String existUserName = employeeExist.getUsername();
        String newUserName = employeeDTO.getUsername();
        if (!existUserName.equals(newUserName) && employeeRepository.existsByUsername(newUserName)) {
            errors.computeIfAbsent("userName", k -> new ArrayList<>()).add(this.messageSource.getMessage(EMPLOYEE_USERNAME_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
        if (!errors.isEmpty()) {
            throw new ValidationDtoException(errors);
        }
    }
}
