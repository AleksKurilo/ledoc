package dk.ledocsystem.model;

public enum Status {
    OK("OK"),
    NOT_OK("NOT_OK");

    private final String value;

    Status(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
