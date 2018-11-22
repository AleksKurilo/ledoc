package dk.ledocsystem.service.api.validation.trade;

import dk.ledocsystem.data.repository.TradeRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueTradeNameDaValidator implements ConstraintValidator<UniqueTradeNameDa, CharSequence> {

    private final TradeRepository tradeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String tradeName = value.toString();
        return !tradeRepository.existsByNameDa(tradeName);
    }
}
