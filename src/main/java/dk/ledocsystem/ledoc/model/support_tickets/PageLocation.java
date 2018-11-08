package dk.ledocsystem.ledoc.model.support_tickets;

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

    private String descr;

    private PageLocation(String descr) {
        this.descr = descr;
    }

    public static PageLocation findLocation(int ordinal) {
        for (PageLocation s : values()) {
            if (s.ordinal() == ordinal) {
                return s;
            }
        }

        return null;
    }

    public String getDescr() {
        return descr;
    }
}
