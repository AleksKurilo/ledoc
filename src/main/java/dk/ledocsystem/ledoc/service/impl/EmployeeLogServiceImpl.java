package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import dk.ledocsystem.ledoc.model.logging.LogType;
import dk.ledocsystem.ledoc.repository.EmployeeLogRepository;
import dk.ledocsystem.ledoc.service.EmployeeLogService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EmployeeLogServiceImpl implements EmployeeLogService {

    private static final Function<Long, Predicate> EMPLOYEE_EQUALS_TO =
            employeeId -> ExpressionUtils.eqConst(QEmployee.employee.id, employeeId);

    final EmployeeService employeeService;
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
    public List<AbstractLogDTO> getAllEmployeeLogs(Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        employeeLogRepository.findAll(predicate).forEach(employeeLog -> {
            Employee actionActor = employeeLog.getEmployee();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setLogType(employeeLog.getLogType());
            log.setLogTypeMessage(employeeLog.getLogType().getDescription());
            log.setActionActor(actionActor.getFirstName() + " " + actionActor.getLastName() + " (" + actionActor.getUsername() + ")");
            log.setDate(sdf.format(employeeLog.getCreated()));
            resultList.add(log);
        });
        return resultList;
    }

    @Override
    public List<EmployeeLog> getAllLogsByTargetId(Long employeeId) {
        return employeeLogRepository.getAllByTargetEmployeeId(employeeId);
    }
}
