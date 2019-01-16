package dk.ledocsystem.service.impl.events.event;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.EmployeeEditDetails;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EditEvent<T> extends EntityEvents<T> {
    private List<EmployeeEditDetails> editDetails;

    public EditEvent(T source, Employee loggedInEmployee, List<SingleDiff> diffList) {
        super(source, loggedInEmployee, LogType.Edit);
        this.editDetails = convertDiff(diffList);
    }

    private List<EmployeeEditDetails> convertDiff(List<SingleDiff> diffList) {
        return diffList.stream()
                .map(diff -> {
                    EmployeeEditDetails editDetails = new EmployeeEditDetails();
                    editDetails.setProperty(diff.getProperty());
                    editDetails.setPreviousValue(diff.getPreviousValue());
                    editDetails.setCurrentValue(diff.getCurrentValue());
                    editDetails.setEditType(diff.getEditType());
                    return editDetails;
                })
                .collect(Collectors.toList());
    }
}
