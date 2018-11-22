package dk.ledocsystem.data.model.equipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.data.exceptions.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApprovalType {
    NO_NEED("No need"),
    BEFORE_USE("Before use"),
    ALL_TIME("All time");

    private final String value;

    @JsonCreator
    public static ApprovalType fromString(String stringValue) {
        for (ApprovalType approvalType : ApprovalType.values()) {
            if (approvalType.value.equalsIgnoreCase(stringValue)) {
                return approvalType;
            }
        }
        throw new InvalidEnumValueException("equipment.approval.type.not.found", stringValue);
    }

    @JsonValue
    public String value() {
        return value;
    }
}
