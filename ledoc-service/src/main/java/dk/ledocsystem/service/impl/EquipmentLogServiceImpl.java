package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.EquipmentEditDetails;
import dk.ledocsystem.data.model.logging.EquipmentLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QEquipmentLog;
import dk.ledocsystem.data.repository.EquipmentLogRepository;
import dk.ledocsystem.data.repository.EquipmentRepository;
import dk.ledocsystem.service.api.EquipmentLogService;
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

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class EquipmentLogServiceImpl implements EquipmentLogService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private static final Function<Long, Predicate> EQUIPMENT_EQUALS_TO =
            equipmentId -> ExpressionUtils.eqConst(QEquipmentLog.equipmentLog.equipment.id, equipmentId);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLogRepository equipmentLogRepository;

    @Override
    public void createLog(Employee loggedInUser, Equipment equipment, LogType logType) {
        EquipmentLog log = new EquipmentLog();
        log.setEmployee(loggedInUser);
        log.setEquipment(equipment);
        log.setLogType(logType);
        equipmentLogRepository.save(log);
    }

    @Override
    public void createEditLog(Employee loggedInUser, Equipment equipment, List<EquipmentEditDetails> editDetails) {
        EquipmentLog log = new EquipmentLog();
        log.setEmployee(loggedInUser);
        log.setEquipment(equipment);
        log.setLogType(LogType.Edit);
        log.setEditDetails(editDetails);
        equipmentLogRepository.save(log);
    }

    @Override
    @Transactional
    public LogsDTO getAllLogsByTargetId(Long equipmentId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, equipmentId.toString()));
        String equipmentName = equipment.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, EQUIPMENT_EQUALS_TO.apply(equipmentId));

        equipmentLogRepository.findAll(combinePredicate).forEach(equipmentLog -> {
            Employee actionActor = equipmentLog.getEmployee();

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(equipmentLog.getId());
            log.setLogType(equipmentLog.getLogType());
            log.setLogTypeMessage(equipmentLog.getLogType().getDescription());
            log.setActionActor(actionActor.getName() + " (" + actionActor.getUsername() + ")");
            log.setDate(equipmentLog.getCreated().format(dateTimeFormatter));
            if (equipmentLog.isEditLog()) {
                log.setEditDetails(mapDetailsToDto(equipmentLog.getEditDetails()));
            }
            resultList.add(log);
        });
        return new LogsDTO(equipmentName, resultList);
    }

    private List<EditDetailsDTO> mapDetailsToDto(List<EquipmentEditDetails> editDetails) {
        return editDetails.stream()
                .map(details -> new EditDetailsDTO(details.getProperty(), details.getPreviousValue(),
                        details.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
