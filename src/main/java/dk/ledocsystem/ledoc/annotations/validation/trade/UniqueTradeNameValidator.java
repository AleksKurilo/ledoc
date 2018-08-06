package dk.ledocsystem.ledoc.annotations.validation.trade;

import dk.ledocsystem.ledoc.repository.TradeRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueTradeNameValidator implements ConstraintValidator<UniqueTradeName, CharSequence> {

    private final TradeRepository tradeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        String tradeName = value.toString();
        return !tradeRepository.findByName(tradeName).isPresent();
    }
}
