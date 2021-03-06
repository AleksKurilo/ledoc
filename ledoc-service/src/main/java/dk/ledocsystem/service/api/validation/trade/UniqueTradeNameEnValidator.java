package dk.ledocsystem.service.api.validation.trade;

import dk.ledocsystem.data.repository.TradeRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueTradeNameEnValidator implements ConstraintValidator<UniqueTradeNameEn, CharSequence> {

    private final TradeRepository tradeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String tradeName = value.toString();
        return !tradeRepository.existsByNameEn(tradeName);
    }
}
