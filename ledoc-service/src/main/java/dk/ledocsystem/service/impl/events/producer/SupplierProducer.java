package dk.ledocsystem.service.impl.events.producer;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import dk.ledocsystem.service.impl.utils.diff.DiffFinder;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import dk.ledocsystem.service.impl.utils.diff.comparators.SupplierComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SupplierProducer {

    private final ApplicationEventPublisher publisher;
    private final DiffFinder diffFinder;

    public void create(Supplier supplier, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(supplier, loggedInEmployee, LogType.Create));
        publisher.publishEvent(new NotificationEvents(loggedInEmployee.getUsername(), "supplier_created"));
        publisher.publishEvent(new NotificationEvents(supplier.getResponsible().getUsername(), "supplier_created"));
    }

    public void read(Supplier supplier, Employee loggedInEmployee, boolean saveLog) {
        publisher.publishEvent(new EntityEvents<>(supplier, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Supplier supplierBeforeEdit, Supplier supplierAfterEdit, Employee loggedInEmployee) {
        List<SingleDiff> diffList = diffFinder.findDiff(supplierBeforeEdit, supplierAfterEdit, SupplierComparator.INSTANCE);
        publisher.publishEvent(new EditEvent<>(supplierAfterEdit, loggedInEmployee, diffList));
        if (!supplierAfterEdit.getResponsible().equals(supplierBeforeEdit.getResponsible())) {
            publisher.publishEvent(new NotificationEvents(supplierAfterEdit.getResponsible().getUsername(),
                    "supplier_responsible_changed", ImmutableMap.of("supplierName", supplierAfterEdit.getName())));
        }
    }

    public void review(Supplier supplier, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(supplier, loggedInEmployee, LogType.Review));
    }

    public void archive(Supplier supplier, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(supplier, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Supplier supplier, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(supplier, loggedInEmployee, LogType.Unarchive));
    }
}
