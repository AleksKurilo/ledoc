package dk.ledocsystem.ledoc.config.security;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserAuthConverter implements AttributeConverter<UserAuthorities, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserAuthorities attribute) {
        return attribute.getCode();
    }

    @Override
    public UserAuthorities convertToEntityAttribute(Integer dbData) {
        return UserAuthorities.fromCode(dbData);
    }
}
