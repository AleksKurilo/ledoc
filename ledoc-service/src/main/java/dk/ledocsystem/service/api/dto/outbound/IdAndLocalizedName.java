package dk.ledocsystem.service.api.dto.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Data
public class IdAndLocalizedName {

    private Long id;

    @JsonIgnore
    private String nameEn;

    @JsonIgnore
    private String nameDa;

    public String getName() {
        Locale locale = LocaleContextHolder.getLocale();

        if (locale.equals(Locale.forLanguageTag("da-DK"))) {
            return nameDa;
        } else {
            return nameEn;
        }
    }
}
