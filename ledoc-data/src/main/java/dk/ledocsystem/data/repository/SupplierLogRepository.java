package dk.ledocsystem.data.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.logging.QSupplierLog;
import dk.ledocsystem.data.model.logging.SupplierLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;

public interface SupplierLogRepository extends JpaRepository<SupplierLog, Long>,
        QuerydslPredicateExecutor<SupplierLog>, QuerydslBinderCustomizer<QSupplierLog> {
    /**
     * Get list of logs by supplier
     *
     * @patam supplierId The ID of affected supplier
     */
    List<SupplierLog> getAllBySupplierId(Long id);

    @Override
    default void customize(QuerydslBindings bindings, QSupplierLog root) {
        bindings.including(root.employee.id, root.supplier.id, root.logType);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
