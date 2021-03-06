package dk.ledocsystem.data.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.data.model.logging.QEmployeeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;

public interface EmployeeLogRepository extends JpaRepository<EmployeeLog, Long>,
        QuerydslPredicateExecutor<EmployeeLog>, QuerydslBinderCustomizer<QEmployeeLog> {

    /**
     * Get list of logs by affected employee.
     *
     * @param employeeId The ID of affected employee.
     */
    List<EmployeeLog> getAllByTargetEmployeeId(Long employeeId);

    @Override
    default void customize(QuerydslBindings bindings, QEmployeeLog root) {
        bindings.including(root.employee.id, root.targetEmployee.id, root.logType);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
