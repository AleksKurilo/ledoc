package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.SupplierLogService;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SupplierEventListener {

    private final SupplierLogService supplierLogService;

    @Async
    @TransactionalEventListener (condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Supplier> event){
        Supplier supplier = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        supplierLogService.createLog(loggedInEmployee, supplier, event.getLogType());
    }
}
