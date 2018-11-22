package dk.ledocsystem.data.model.support_tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PageLocation {
    DASHBOARD ("Dashboard page"),
    EMPLOYEES("Employees page"),
    EQUIPMENT("Equipment page"),
    SUPPLIERS("Suppliers page"),
    DOCUMENTS("Documents page"),
    CARS("Cars page"),
    IMPROVEMENT("Improvement page"),
    SKILLS("Skill page"),
    LOCATION("Location page");

    private String description;

    public static PageLocation findLocation(int ordinal) {
        for (PageLocation s : values()) {
            if (s.ordinal() == ordinal) {
                return s;
            }
        }

        return null;
    }
}
