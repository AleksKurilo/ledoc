package dk.ledocsystem.data.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.logging.EquipmentLog;
import dk.ledocsystem.data.model.logging.QEquipmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

public interface EquipmentLogRepository extends JpaRepository<EquipmentLog, Long>,
        QuerydslPredicateExecutor<EquipmentLog>, QuerydslBinderCustomizer<QEquipmentLog> {

    @Override
    default void customize(QuerydslBindings bindings, QEquipmentLog root) {
        bindings.including(root.employee.id, root.equipment.id, root.logType);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

}
