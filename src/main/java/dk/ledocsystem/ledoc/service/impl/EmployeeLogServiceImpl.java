package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.dto.EmployeeLogDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.employee.Employee;
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

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EmployeeLogServiceImpl implements EmployeeLogService {

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
    public EmployeeLogDTO getAllEmployeeLogs(Long employeeId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        String employeeName = "";
        Employee currentUser = employeeService.getById(employeeId)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, employeeId.toString()));
        employeeName = currentUser.getName();
        employeeLogRepository.findAll(predicate).forEach(employeeLog -> {
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
        EmployeeLogDTO result = new EmployeeLogDTO(employeeName, resultList);
        return result;
    }

    @Override
    public List<EmployeeLog> getAllLogsByTargetId(Long employeeId) {
        return employeeLogRepository.getAllByTargetEmployeeId(employeeId);
    }
}
