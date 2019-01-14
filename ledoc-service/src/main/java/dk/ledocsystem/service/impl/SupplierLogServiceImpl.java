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
import dk.ledocsystem.service.api.dto.outbound.LogsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

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
    public LogsDTO getAllSupplierLogs(Long supplierId, Predicate predicate) {

        return null;
    }

    @Override
    public List<? extends AbstractLog> getAllLogsByTargetId(Long targetId) {
        return null;
    }
}
