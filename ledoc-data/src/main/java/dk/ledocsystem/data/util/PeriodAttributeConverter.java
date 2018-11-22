package dk.ledocsystem.data.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Period;

@Converter(autoApply = true)
public class PeriodAttributeConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period attribute) {
        return (attribute == null) ? null : attribute.toString();
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Period.parse(dbData);
    }
}
