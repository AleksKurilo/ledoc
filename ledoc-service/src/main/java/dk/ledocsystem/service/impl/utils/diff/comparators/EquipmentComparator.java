package dk.ledocsystem.service.impl.utils.diff.comparators;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.impl.utils.diff.Diff;
import dk.ledocsystem.service.impl.utils.diff.EntityComparator;

public class EquipmentComparator implements EntityComparator<Equipment> {

    public static final EquipmentComparator INSTANCE = new EquipmentComparator();

    @Override
    public Diff compare(Equipment left, Equipment right) {
        Diff diff = new Diff();

        compareSimple(left.getName(), right.getName(), "Name", diff);
        compareSimple(left.getIdNumber(), right.getIdNumber(), "ID number", diff);
        compareSimple(left.getSerialNumber(), right.getSerialNumber(), "Serial number", diff);
        compareSimple(left.getLocalId(), right.getLocalId(), "Local ID", diff);
        compareSimple(left.getCategory(), right.getCategory(), "Category", diff);
        compareSimple(left.getManufacturer(), right.getManufacturer(), "Manufacturer", diff);
        compareSimple(left.getPrice(), right.getPrice(), "Price", diff);
        compareSimple(left.getSupplier(), right.getSupplier(), "Supplier", diff);
        compareSimple(left.getResponsible(), right.getResponsible(), "Responsible", diff);
        compareSimple(left.getPurchaseDate(), right.getPurchaseDate(), "Purchase date", diff);
        compareSimple(left.getWarrantyDate(), right.getWarrantyDate(), "Warranty date", diff);
        compareSimple(left.getAuthenticationType(), right.getAuthenticationType(), "Authentication type", diff);
        compareSimple(left.getLocation(), right.getLocation(), "Location", diff);
        compareSimple(left.getReviewTemplate(), right.getReviewTemplate(), "Review template", diff);
        compareSimple(left.getApprovalType(), right.getApprovalType(), "Approval type", diff);
        compareSimple(left.getApprovalRate(), right.getApprovalRate(), "Approval rate", diff);
        compareSimple(left.getComment(), right.getComment(), "Comment", diff);
        compareSimple(left.getReadyToLoan(), right.getReadyToLoan(), "Ready to loan", diff);

        return diff;
    }
}
