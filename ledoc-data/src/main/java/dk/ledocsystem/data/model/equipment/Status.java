package dk.ledocsystem.data.model.equipment;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    OK("OK"),
    NOT_OK("NOT OK");

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static Status fromString(String statusString) {
        for (Status status : values()) {
            if (status.value.equalsIgnoreCase(statusString)) {
                return status;
            }
        }
        return null;
    }
}
