package dk.ledocsystem.ledoc.annotations.validation.trade;

import dk.ledocsystem.ledoc.repository.TradeRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
public class UniqueTradeNameDkValidator implements ConstraintValidator<UniqueTradeNameDa, CharSequence> {

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
