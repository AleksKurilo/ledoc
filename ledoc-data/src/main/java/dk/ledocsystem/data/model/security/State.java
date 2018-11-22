package dk.ledocsystem.data.model.security;

public enum State {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    private final String value;

    State(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
