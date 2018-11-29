package dk.ledocsystem.service.impl.events.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class NotificationEvents {
    private String recipient;
    private String emailKey;
    private Map<String, Object> model;

    public NotificationEvents(String recipient, String emailKey) {
        this(recipient, emailKey, new HashMap<>());
    }
}
