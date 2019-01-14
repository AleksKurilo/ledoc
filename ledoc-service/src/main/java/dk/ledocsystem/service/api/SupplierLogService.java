package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.SupplierLog;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.dto.outbound.LogsDTO;

public interface SupplierLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee - employee who performed an action
     * @param supplier         - affected supplier
     * @param logType          - the type of action
     * @return Newly created {@link SupplierLog}
     */
    SupplierLog createLog(Employee loggedInEmployee, Supplier supplier, LogType logType);

    /**
     * Returns the required log information to display
     *
     * @param supplierId - id of target supplier
     * @param predicate
     * @return Name of employee and list of log properties
     */
    LogsDTO getAllSupplierLogs(Long supplierId, Predicate predicate);
}
