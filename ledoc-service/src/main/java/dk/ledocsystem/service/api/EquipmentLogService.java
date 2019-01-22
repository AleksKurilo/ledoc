package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.EquipmentEditDetails;
import dk.ledocsystem.data.model.logging.LogType;

import java.util.List;

public interface EquipmentLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee employee who performed an action
     * @param equipment        affected equipment
     * @param logType          the type of action
     */
    void createLog(Employee loggedInEmployee, Equipment equipment, LogType logType);

    /**
     * @param loggedInEmployee employee who performed an action
     * @param equipment        affected equipment
     * @param editDetails      List of edit details
     */
    void createEditLog(Employee loggedInEmployee, Equipment equipment, List<EquipmentEditDetails> editDetails);
}
