package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
class CustomerEditDtoValidator extends BaseValidator<CustomerEditDTO> {

    private final CustomerRepository customerRepository;

    @Override
    protected void validateInner(CustomerEditDTO dto, Map<String, List<String>> messages) {
        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, dto.getId().toString()));
        String existName = customer.getName();
        String newName = dto.getName();
        if (!existName.equals(newName) && customerRepository.existsByName(newName)) {
            messages.computeIfAbsent("name",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(CUSTOMER_NAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }

        String existCvr = customer.getCvr();
        String newCvr = dto.getCvr();
        if (!existCvr.equals(newCvr) && customerRepository.existsByCvr(newCvr)) {
            messages.computeIfAbsent("cvr",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(CVR_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}
