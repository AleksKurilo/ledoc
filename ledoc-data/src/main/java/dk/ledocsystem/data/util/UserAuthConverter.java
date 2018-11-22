package dk.ledocsystem.data.util;

import dk.ledocsystem.data.model.security.UserAuthorities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
class UserAuthConverter implements AttributeConverter<UserAuthorities, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserAuthorities attribute) {
        return attribute.getCode();
    }

    @Override
    public UserAuthorities convertToEntityAttribute(Integer dbData) {
        return UserAuthorities.fromCode(dbData);
    }
}
