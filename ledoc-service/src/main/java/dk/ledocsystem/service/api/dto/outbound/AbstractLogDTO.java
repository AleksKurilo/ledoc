package dk.ledocsystem.service.api.dto.outbound;

import dk.ledocsystem.data.model.logging.LogType;
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
