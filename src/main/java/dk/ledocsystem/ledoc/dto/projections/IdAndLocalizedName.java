package dk.ledocsystem.ledoc.dto.projections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public interface IdAndLocalizedName {

    Long getId();

    @JsonIgnore
    String getNameEn();

    @JsonIgnore
    String getNameDa();

    default String getName() {
        Locale locale = LocaleContextHolder.getLocale();

        if (locale.equals(Locale.forLanguageTag("da-DK"))) {
            return getNameDa();
        } else {
            return getNameEn();
        }
    }
}
