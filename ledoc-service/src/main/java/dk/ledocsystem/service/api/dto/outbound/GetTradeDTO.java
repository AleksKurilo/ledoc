package dk.ledocsystem.service.api.dto.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Data
public class GetTradeDTO {

    private Long id;

    @JsonIgnore
    private String nameEn;

    @JsonIgnore
    private String nameDa;

    public String getName() {
        Locale locale = LocaleContextHolder.getLocale();

        if (locale.equals(Locale.forLanguageTag("da-DK"))) {
            return getNameDa();
        } else {
            return getNameEn();
        }
    }
}
