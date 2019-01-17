package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.DocumentLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QDocumentLog;
import dk.ledocsystem.data.repository.DocumentLogRepository;
import dk.ledocsystem.data.repository.DocumentRepository;
import dk.ledocsystem.service.api.DocumentLogService;
import dk.ledocsystem.service.api.dto.outbound.logs.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.outbound.logs.LogsDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DocumentLogServiceImpl implements DocumentLogService {

    private static final Function<Long, Predicate> DOCUMENT_EQUALS_TO =
            documentId -> ExpressionUtils.eqConst(QDocumentLog.documentLog.document.id, documentId);

    private final DocumentRepository documentRepository;
    private final DocumentLogRepository documentLogRepository;

    @Override
    public void createLog(Employee loggedInUser, Document document, LogType logType) {
        DocumentLog log = new DocumentLog();
        log.setEmployee(loggedInUser);
        log.setDocument(document);
        log.setLogType(logType);
        documentLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllLogsByTargetId(Long documentId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        String documentName = document.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, DOCUMENT_EQUALS_TO.apply(documentId));

        documentLogRepository.findAll(combinePredicate).forEach(documentLog -> {
            Employee actionActor = documentLog.getEmployee();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(documentLog.getId());
            log.setLogType(documentLog.getLogType());
            log.setLogTypeMessage(documentLog.getLogType().getDescription());
            log.setActionActor(actionActor.getName() + " (" + actionActor.getUsername() + ")");
            log.setDate(sdf.format(documentLog.getCreated()));
            resultList.add(log);
        });
        return new LogsDTO(documentName, resultList);
    }
}
