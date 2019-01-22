package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.EmployeeEditDetails;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QEmployeeLog;
import dk.ledocsystem.data.repository.EmployeeLogRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.api.dto.outbound.logs.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.outbound.logs.EditDetailsDTO;
import dk.ledocsystem.service.api.dto.outbound.logs.LogsDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class EmployeeLogServiceImpl implements EmployeeLogService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private static final Function<Long, Predicate> EMPLOYEE_EQUALS_TO =
            employeeId -> ExpressionUtils.eqConst(QEmployeeLog.employeeLog.targetEmployee.id, employeeId);

    private final EmployeeRepository employeeRepository;
    private final EmployeeLogRepository employeeLogRepository;

    @Override
    public void createLog(Employee loggedInUser, Employee targetUser, LogType logType) {
        EmployeeLog log = new EmployeeLog();
        log.setEmployee(loggedInUser);
        log.setLogType(logType);
        log.setTargetEmployee(targetUser);
        employeeLogRepository.save(log);
    }

    @Override
    public void createEditLog(Employee loggedInUser, Employee targetUser, List<EmployeeEditDetails> editDetails) {
        EmployeeLog log = new EmployeeLog();
        log.setEmployee(loggedInUser);
        log.setLogType(LogType.Edit);
        log.setEditDetails(editDetails);
        log.setTargetEmployee(targetUser);
        employeeLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllLogsByTargetId(Long employeeId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        Employee currentUser = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        String employeeName = currentUser.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, EMPLOYEE_EQUALS_TO.apply(employeeId));

        employeeLogRepository.findAll(combinePredicate).forEach(employeeLog -> {
            Employee actionActor = employeeLog.getEmployee();

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(employeeLog.getId());
            log.setLogType(employeeLog.getLogType());
            log.setLogTypeMessage(employeeLog.getLogType().getDescription());
            log.setActionActor(actionActor.getName() + " (" + actionActor.getUsername() + ")");
            log.setDate(employeeLog.getCreated().format(dateTimeFormatter));
            if (employeeLog.isEditLog()) {
                log.setEditDetails(mapDetailsToDto(employeeLog.getEditDetails()));
            }
            resultList.add(log);
        });
        return new LogsDTO(employeeName, resultList);
    }

    private List<EditDetailsDTO> mapDetailsToDto(List<EmployeeEditDetails> editDetails) {
        return editDetails.stream()
                .map(details -> new EditDetailsDTO(details.getProperty(), details.getPreviousValue(),
                        details.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
