package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.SupplierEditDetails;
import dk.ledocsystem.data.model.logging.SupplierLog;
import dk.ledocsystem.data.model.supplier.Supplier;

import java.util.List;

public interface SupplierLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee - employee who performed an action
     * @param supplier         - affected supplier
     * @param logType          - the type of action
     * @return Newly created {@link SupplierLog}
     */
    SupplierLog createLog(Employee loggedInEmployee, Supplier supplier, LogType logType);

    /**
     * @param loggedInEmployee employee who performed an action
     * @param supplier        affected supplier
     * @param editDetails      List of edit details
     */
    void createEditLog(Employee loggedInEmployee, Supplier supplier, List<SupplierEditDetails> editDetails);
}
