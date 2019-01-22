package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QSupplierLog;
import dk.ledocsystem.data.model.logging.SupplierEditDetails;
import dk.ledocsystem.data.model.logging.SupplierLog;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.data.repository.SupplierLogRepository;
import dk.ledocsystem.data.repository.SupplierRepository;
import dk.ledocsystem.service.api.SupplierLogService;
import dk.ledocsystem.service.api.dto.outbound.logs.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.outbound.logs.EditDetailsDTO;
import dk.ledocsystem.service.api.dto.outbound.logs.LogsDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_ID_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class SupplierLogServiceImpl implements SupplierLogService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private static final Function<Long, Predicate> SUPPLIER_EQUALS_TO =
            supplierId -> ExpressionUtils.eqConst(QSupplierLog.supplierLog.supplier.id, supplierId);

    final SupplierRepository supplierRepository;
    private final SupplierLogRepository supplierLogRepository;

    @Override
    public SupplierLog createLog(Employee loggedInEmployee, Supplier supplier, LogType logType) {
        SupplierLog log = new SupplierLog();
        log.setEmployee(loggedInEmployee);
        log.setSupplier(supplier);
        log.setLogType(logType);
        return supplierLogRepository.save(log);
    }

    @Override
    public void createEditLog(Employee loggedInEmployee, Supplier supplier, List<SupplierEditDetails> editDetails) {
        SupplierLog log = new SupplierLog();
        log.setEmployee(loggedInEmployee);
        log.setSupplier(supplier);
        log.setLogType(LogType.Edit);
        log.setEditDetails(editDetails);
        supplierLogRepository.save(log);
    }


    @Override
    public LogsDTO getAllLogsByTargetId(Long supplierId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, supplierId.toString()));
        String supplierName = supplier.getName();

        Predicate combinePredicate = ExpressionUtils.and(predicate, SUPPLIER_EQUALS_TO.apply(supplierId));

        supplierLogRepository.findAll(combinePredicate).forEach((supplierLog -> {
            Employee actionActor = supplierLog.getEmployee();

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(supplierLog.getId());
            log.setLogType(supplierLog.getLogType());
            log.setLogTypeMessage(supplierLog.getLogType().getDescription());
            log.setActionActor(actionActor.getName() + " (" + actionActor.getUsername() + ")");
            log.setDate(supplierLog.getCreated().format(dateTimeFormatter));
            if (supplierLog.isEditLog()) {
                log.setEditDetails(mapDetailsToDto(supplierLog.getEditDetails()));
            }
            resultList.add(log);
        }));

        return new LogsDTO(supplierName, resultList);
    }

    private List<EditDetailsDTO> mapDetailsToDto(List<SupplierEditDetails> editDetails) {
        return editDetails.stream()
                .map(details -> new EditDetailsDTO(details.getProperty(), details.getPreviousValue(),
                        details.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
