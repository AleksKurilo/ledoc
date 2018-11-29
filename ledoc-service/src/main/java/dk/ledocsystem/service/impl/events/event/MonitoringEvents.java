package dk.ledocsystem.service.impl.events.event;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@Getter
@AllArgsConstructor
public class MonitoringEvents<T> implements ResolvableTypeProvider {
    private T source;
    private Employee follower;
    private boolean forced;
    private boolean followed;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(source));
    }
}
