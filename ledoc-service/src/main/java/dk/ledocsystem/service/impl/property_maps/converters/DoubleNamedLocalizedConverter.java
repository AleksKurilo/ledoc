package dk.ledocsystem.service.impl.property_maps.converters;

import dk.ledocsystem.data.model.DoubleNamed;
import org.modelmapper.AbstractConverter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class DoubleNamedLocalizedConverter extends AbstractConverter<DoubleNamed, String> {

    public static final DoubleNamedLocalizedConverter INSTANCE = new DoubleNamedLocalizedConverter();

    @Override
    public String convert(DoubleNamed source) {
        if (source != null) {
            Locale locale = LocaleContextHolder.getLocale();

            if (locale.equals(Locale.forLanguageTag("da-DK"))) {
                return source.getNameDa();
            } else {
                return source.getNameEn();
            }
        }
        return null;
    }
}
