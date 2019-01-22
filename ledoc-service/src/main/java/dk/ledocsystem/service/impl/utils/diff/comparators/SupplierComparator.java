package dk.ledocsystem.service.impl.utils.diff.comparators;

import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.impl.utils.diff.Diff;
import dk.ledocsystem.service.impl.utils.diff.EntityComparator;

public class SupplierComparator implements EntityComparator<Supplier> {

    public static final SupplierComparator INSTANCE = new SupplierComparator();

    @Override
    public Diff compare(Supplier left, Supplier right) {
        Diff diff = new Diff();

        compareSimple(left.getName(), right.getName(), "Name", diff);
        compareSimple(left.getDescription(), right.getDescription(), "Description", diff);
        compareSimple(left.getContactPhone(), right.getContactPhone(), "ContactPhone", diff);
        compareSimple(left.getApprovalRate(), right.getApprovalRate(), "ApprovalRate", diff);
        compareSimple(left.getNextReviewDate(), right.getNextReviewDate(), "NextReviewDate", diff);
        compareSimple(left.isArchived(), right.isArchived(), "Archived", diff);
        compareSimple(left.getArchiveReason(), right.getArchiveReason(), "ArchiveReason", diff);
        compareSimple(left.isReview(), right.isReview(), "Review", diff);
        compareSimple(left.getCategory(), right.getCategory(), "Category", diff);
        compareSimple(left.getResponsible(), right.getResponsible(), "Responsible", diff);
        compareSimple(left.getReviewResponsible(), right.getReviewResponsible(), "ReviewResponsible", diff);
        compareSimple(left.getReviewTemplate(), right.getReviewTemplate(), "Review template", diff);
        compareSimple(left.getApprovalRate(), right.getApprovalRate(), "Approval rate", diff);
        return diff;
    }
}
