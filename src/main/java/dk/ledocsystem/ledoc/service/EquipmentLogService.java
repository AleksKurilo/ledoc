package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.LogsDTO;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.logging.EquipmentLog;
import dk.ledocsystem.ledoc.model.logging.LogType;

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
