package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.dto.inbound.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.inbound.EmployeeLogDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QEmployeeLog;
import dk.ledocsystem.data.repository.EmployeeLogRepository;
import dk.ledocsystem.service.api.EmployeeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EmployeeLogServiceImpl implements EmployeeLogService {

    private static final Function<Long, Predicate> EMPLOYEE_EQUALS_TO =
            employeeId -> ExpressionUtils.eqConst(QEmployeeLog.employeeLog.targetEmployee.id, employeeId);

    private final EmployeeRepository employeeRepository;
    private final EmployeeLogRepository employeeLogRepository;

    @Override
    public EmployeeLog createLog(Employee loggedInUser, Employee targetUser, LogType logType) {
        EmployeeLog log = new EmployeeLog();
        log.setEmployee(loggedInUser);
        log.setLogType(logType);
        log.setTargetEmployee(targetUser);
        return employeeLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllEmployeeLogs(Long employeeId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        Employee currentUser = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        String employeeName = currentUser.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, EMPLOYEE_EQUALS_TO.apply(employeeId));

        employeeLogRepository.findAll(combinePredicate).forEach(employeeLog -> {
            Employee actionActor = employeeLog.getEmployee();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(employeeLog.getId());
            log.setLogType(employeeLog.getLogType());
            log.setLogTypeMessage(employeeLog.getLogType().getDescription());
            log.setActionActor(actionActor.getFirstName() + " " + actionActor.getLastName() + " (" + actionActor.getUsername() + ")");
            log.setDate(sdf.format(employeeLog.getCreated()));
            resultList.add(log);
        });
        return new EmployeeLogDTO(employeeName, resultList);
    }

    @Override
    public List<EmployeeLog> getAllLogsByTargetId(Long employeeId) {
        return employeeLogRepository.getAllByTargetEmployeeId(employeeId);
    }
}
