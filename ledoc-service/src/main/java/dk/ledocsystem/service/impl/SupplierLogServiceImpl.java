package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.AbstractLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.model.logging.QSupplierLog;
import dk.ledocsystem.data.model.logging.SupplierLog;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.data.repository.SupplierLogRepository;
import dk.ledocsystem.data.repository.SupplierRepository;
import dk.ledocsystem.service.api.SupplierLogService;
import dk.ledocsystem.service.api.dto.outbound.AbstractLogDTO;
import dk.ledocsystem.service.api.dto.outbound.LogsDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SupplierLogServiceImpl implements SupplierLogService {

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
    @Transactional
    public LogsDTO getAllSupplierLogs(Long supplierId, Predicate predicate) {
        List<AbstractLogDTO> resultList = new ArrayList<>();
        String supplierName = "";
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, supplierId.toString()));

        Predicate combinePredicate = ExpressionUtils.and(predicate, SUPPLIER_EQUALS_TO.apply(supplierId));

        supplierLogRepository.findAll(combinePredicate).forEach(supplierLog -> {
            Employee actionActor = supplierLog.getEmployee();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            AbstractLogDTO log = new AbstractLogDTO();
            log.setId(supplierLog.getId());
            log.setLogType(supplierLog.getLogType());
            log.setLogTypeMessage(supplierLog.getLogType().getDescription());
            log.setActionActor(actionActor.getFirstName() + " " + actionActor.getLastName() + " (" + actionActor.getUsername() + ")");
            log.setDate(dateFormat.format(supplierLog.getCreated()));
            resultList.add(log);
        });
        LogsDTO result = new LogsDTO(supplierName, resultList);
        return result;
    }

    @Override
    public List<? extends AbstractLog> getAllLogsByTargetId(Long targetId) {
        return supplierLogRepository.getAllBySupplierId(targetId);
    }
}
