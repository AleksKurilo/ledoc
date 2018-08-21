package dk.ledocsystem.ledoc.dto.location;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dk.ledocsystem.ledoc.model.LocationType;

import java.io.IOException;

/**
 * Dynamic deserializer for {@link LocationEditDTO#address} field.
 * Uses proper {@link AddressDTO} implementation depending on operation, which can be either edit of existing address
 * or creating new address.
 */
class AddressDeserializer extends JsonDeserializer<AddressDTO> {

    @Override
    public AddressDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        LocationEditDTO locationDTO = (LocationEditDTO) jp.getParsingContext().getParent().getCurrentValue();
        LocationType type = locationDTO.getType();

        Class<? extends AddressDTO> addressType;
        if (type == LocationType.ADDRESS) {
            addressType = AddressCreateDTO.class;
        } else {
            addressType = AddressEditDTO.class;
        }
        return jp.getCodec().readValue(jp, addressType);
    }
}
