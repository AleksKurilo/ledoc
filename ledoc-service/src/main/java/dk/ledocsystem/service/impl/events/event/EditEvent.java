package dk.ledocsystem.service.impl.events.event;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import lombok.Getter;

import java.util.List;

@Getter
public class EditEvent<T> extends EntityEvents<T> {
    private List<SingleDiff> diffList;

    public EditEvent(T source, Employee loggedInEmployee, List<SingleDiff> diffList) {
        super(source, loggedInEmployee, LogType.Edit);
        this.diffList = diffList;
    }
}
