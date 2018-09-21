package dk.ledocsystem.ledoc.model.equipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import dk.ledocsystem.ledoc.exceptions.InvalidApprovalType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApprovalType {
    NO_NEED("no need"),
    BEFORE_USE("before use"),
    ALL_TIME("all time");

    private final String value;

    @JsonCreator
    public static ApprovalType fromString(String stringValue) {
        for (ApprovalType approvalType : ApprovalType.values()) {
            if (approvalType.value.equalsIgnoreCase(stringValue)) {
                return approvalType;
            }
        }
        throw new InvalidApprovalType(stringValue);
    }
}
