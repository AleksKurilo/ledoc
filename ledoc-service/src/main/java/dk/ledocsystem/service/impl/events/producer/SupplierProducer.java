package dk.ledocsystem.service.impl.events.producer;

import dk.ledocsystem.data.model.supplier.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplierProducer {

    private final ApplicationEventPublisher publisher;

    public void create(Supplier supplier, )
}
