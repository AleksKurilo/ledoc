package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.dto.LogsDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.logging.EquipmentLog;
import dk.ledocsystem.ledoc.model.logging.LogType;
import dk.ledocsystem.ledoc.model.logging.QEquipmentLog;
import dk.ledocsystem.ledoc.repository.EquipmentLogRepository;
import dk.ledocsystem.ledoc.service.EquipmentLogService;
import dk.ledocsystem.ledoc.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class EquipmentLogServiceImpl implements EquipmentLogService {

    private static final Function<Long, Predicate> EQUIPMENT_EQUALS_TO =
            equipmentId -> ExpressionUtils.eqConst(QEquipmentLog.equipmentLog.equipment.id, equipmentId);

    final EquipmentService equipmentService;
    private final EquipmentLogRepository equipmentLogRepository;

    @Override
    public EquipmentLog createLog(Employee loggedInUser, Equipment equipment, LogType logType) {
        EquipmentLog log = new EquipmentLog();
        log.setEmployee(loggedInUser);
        log.setEquipment(equipment);
        log.setLogType(logType);
        return equipmentLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllEquipmentLogs(Long equipmentId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        String equipmentName = "";
        Equipment equipment = equipmentService.getById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        equipmentName = equipment.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, EQUIPMENT_EQUALS_TO.apply(equipmentId));

        equipmentLogRepository.findAll(combinePredicate).forEach(equipmentLog -> {
            Employee actionActor = equipmentLog.getEmployee();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(equipmentLog.getId());
            log.setLogType(equipmentLog.getLogType());
            log.setLogTypeMessage(equipmentLog.getLogType().getDescription());
            log.setActionActor(actionActor.getFirstName() + " " + actionActor.getLastName() + " (" + actionActor.getUsername() + ")");
            log.setDate(sdf.format(equipmentLog.getCreated()));
            resultList.add(log);
        });
        LogsDTO result = new LogsDTO(equipmentName, resultList);
        return result;
    }

    @Override
    public List<EquipmentLog> getAllLogsByTargetId(Long equipmentId) {
        return equipmentLogRepository.getAllByEquipmentId(equipmentId);
    }
}
