package dk.ledocsystem.service.impl.events.event;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@Getter
@AllArgsConstructor
public class EntityEvents<T> implements ResolvableTypeProvider {
    private T source;
    private Employee loggedInEmployee;
    private LogType logType;
    private boolean saveLog;

    public EntityEvents(T source, Employee loggedInEmployee, LogType logType) {
        this(source, loggedInEmployee, logType, true);
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(source));
    }
}
