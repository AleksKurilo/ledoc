package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.model.logging.LogType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AbstractLogDTO {
    private Long id;
    private LogType logType;
    private String logTypeMessage;
    private String actionActor;
    private String date;
}
