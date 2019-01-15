package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;

public interface EquipmentLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee employee who performed an action
     * @param equipment        affected equipment
     * @param logType          the type of action
     */
    void createLog(Employee loggedInEmployee, Equipment equipment, LogType logType);
}
