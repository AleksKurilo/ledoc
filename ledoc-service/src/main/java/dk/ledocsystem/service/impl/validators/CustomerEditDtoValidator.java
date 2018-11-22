package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.customer.CustomerEditDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
class CustomerEditDtoValidator extends BaseValidator<CustomerEditDTO> {

    private final CustomerRepository customerRepository;

    @Override
    protected void validateInner(CustomerEditDTO dto, Map<String, List<String>> messages) {
        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, dto.getId().toString()));
        Locale locale = getLocale();
        String existName = customer.getName();
        String newName = dto.getName();
        if (!existName.equals(newName) && customerRepository.existsByName(newName)) {
            messages.computeIfAbsent("name", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(CUSTOMER_NAME_IS_ALREADY_IN_USE, null, locale));
        }

        String existCvr = customer.getCvr();
        String newCvr = dto.getCvr();
        if (!existCvr.equals(newCvr) && customerRepository.existsByCvr(newCvr)) {
            messages.computeIfAbsent("cvr", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(CVR_IS_ALREADY_IN_USE, null, locale));
        }
    }
}
