package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.dto.outbound.logs.LogsDTO;

public interface AbstractLogService {

    /**
     * Returns the required log information to display
     *
     * @param targetId  ID of target entity
     * @param predicate QueryDSL predicate
     * @return Name of employee and list of logs
     */
    LogsDTO getAllLogsByTargetId(Long targetId, Predicate predicate);
}
