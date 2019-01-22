package dk.ledocsystem.service.impl.utils.diff.comparators;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.impl.utils.diff.Diff;
import dk.ledocsystem.service.impl.utils.diff.EntityComparator;

public class DocumentComparator implements EntityComparator<Document> {

    public static final DocumentComparator INSTANCE = new DocumentComparator();

    @Override
    public Diff compare(Document left, Document right) {
        Diff diff = new Diff();

        compareSimple(left.getName(), right.getName(), "Name", diff);
        compareSimple(left.getIdNumber(), right.getIdNumber(), "ID number", diff);
        compareSimple(left.getComment(), right.getComment(), "Comment", diff);
        compareSimple(left.getType(), right.getType(), "Type", diff);
        compareSimple(left.getSource(), right.getSource(), "Source", diff);
        compareSimple(left.getStatus(), right.getStatus(), "Status", diff);
        compareSimple(left.isPersonal(), right.isPersonal(), "Private", diff);
        compareSimple(left.getApprovalRate(), right.getApprovalRate(), "Approval rate", diff);
        compareSimple(left.getResponsible(), right.getResponsible(), "Responsible", diff);
        compareSimple(left.getCategory(), right.getCategory(), "Category", diff);
        compareSimple(left.getSubcategory(), right.getSubcategory(), "Subcategory", diff);
        compareSimple(left.getReviewTemplate(), right.getReviewTemplate(), "Review template", diff);

        compareSets(left.getLocations(), right.getLocations(), "Locations", diff);
        compareSets(left.getTrades(), right.getTrades(), "Trades", diff);

        return diff;
    }
}
