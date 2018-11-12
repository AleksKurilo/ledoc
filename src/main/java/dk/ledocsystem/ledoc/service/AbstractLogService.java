package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.model.logging.AbstractLog;

import java.util.List;

public interface AbstractLogService {

    /**
     * Returns list of all logs actioned target
     * @param targetId - the ID of actioned target
     * @return list of all logs
     */
    List<? extends AbstractLog> getAllLogsByTargetId(Long targetId);

}
