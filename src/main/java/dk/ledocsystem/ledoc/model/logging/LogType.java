package dk.ledocsystem.ledoc.model.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogType {
    Create("was created by"),
    Edit("was edited by"),
    Archive("was archived by"),
    Unarchive("was unarchived by"),
    Read("has been read by"),
    Review("has been reviewed by"),
    Delete("has been deleted by");

    private String description;

    public static LogType findLocation(int ordinal) {
        for (LogType s : values()) {
            if (s.ordinal() == ordinal) {
                return s;
            }
        }

        return null;
    }
}