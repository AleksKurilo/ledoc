package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.EquipmentLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.inbound.LogsDTO;

public interface EquipmentLogService extends AbstractLogService {

    /**
     *
     * @param loggedInEmployee - employee who performed an action
     * @param equipment - affected equipment
     * @param logType - the type of action
     * @return Newly created {@link EquipmentLog}
     */
    EquipmentLog createLog(Employee loggedInEmployee, Equipment equipment, LogType logType);

    /**
     * Returns the required log information to display
     * @param equipmentId - id of target equipment
     * @param predicate
     * @return Name of employee and list of log properties
     */
    LogsDTO getAllEquipmentLogs(Long equipmentId, Predicate predicate);
}
