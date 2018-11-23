package dk.ledocsystem.service.api.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LogsDTO {
    private String name;
    private List<AbstractLogDTO> logs;
}
