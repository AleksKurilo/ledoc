package dk.ledocsystem.service.impl.events.event_listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplierEventListener {

    private final SupplierLogService supplierLogService;
}
