package dk.ledocsystem.service.api.dto.outbound.logs;

import dk.ledocsystem.data.model.logging.LogType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AbstractLogDTO {
    private Long id;
    private LogType logType;
    private String logTypeMessage;
    private String actionActor;
    private String date;
    private List<EditDetailsDTO> editDetails;
}
