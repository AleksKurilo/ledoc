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
import dk.ledocsystem.service.api.dto.inbound.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.inbound.LogsDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class DocumentLogServiceImpl implements DocumentLogService {

    private static final Function<Long, Predicate> DOCUMENT_EQUALS_TO =
            documentId -> ExpressionUtils.eqConst(QDocumentLog.documentLog.document.id, documentId);

    final DocumentRepository documentRepository;
    private final DocumentLogRepository documentLogRepository;

    @Override
    public DocumentLog createLog(Employee loggedInUser, Document document, LogType logType) {
        DocumentLog log = new DocumentLog();
        log.setEmployee(loggedInUser);
        log.setDocument(document);
        log.setLogType(logType);
        return documentLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllDocumentLogs(Long documentId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        String documentName = "";
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, documentId.toString()));
        documentName = document.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, DOCUMENT_EQUALS_TO.apply(documentId));

        documentLogRepository.findAll(combinePredicate).forEach(documentLog -> {
            Employee actionActor = documentLog.getEmployee();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(documentLog.getId());
            log.setLogType(documentLog.getLogType());
            log.setLogTypeMessage(documentLog.getLogType().getDescription());
            log.setActionActor(actionActor.getFirstName() + " " + actionActor.getLastName() + " (" + actionActor.getUsername() + ")");
            log.setDate(sdf.format(documentLog.getCreated()));
            resultList.add(log);
        });
        LogsDTO result = new LogsDTO(documentName, resultList);
        return result;
    }

    @Override
    public List<DocumentLog> getAllLogsByTargetId(Long documentId) {
        return documentLogRepository.getAllByDocumentId(documentId);
    }
}
