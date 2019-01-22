package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.SupplierEditDetails;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.SupplierLogService;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplierEventListener {

    private final SupplierLogService supplierLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog and #event.logType != T(dk.ledocsystem.data.model.logging.LogType).Edit")
    public void onApplicationEvent(EntityEvents<Supplier> event) {
        Supplier supplier = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        supplierLogService.createLog(loggedInEmployee, supplier, event.getLogType());
    }

    @Async
    @TransactionalEventListener
    public void onEditEvent(EditEvent<Supplier> event) {
        Supplier supplier = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        supplierLogService.createEditLog(loggedInEmployee, supplier, convertDiff(event.getDiffList()));
    }

    private List<SupplierEditDetails> convertDiff(List<SingleDiff> diffList) {
        return diffList.stream()
                .map(diff -> new SupplierEditDetails(diff.getProperty(), diff.getPreviousValue(), diff.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
