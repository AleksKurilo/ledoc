package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.model.logging.LogType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class EmployeeLogDTO {
    private String name;
    private List<AbstractLogDTO> logs;
}
