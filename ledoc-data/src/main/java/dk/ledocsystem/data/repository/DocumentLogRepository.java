package dk.ledocsystem.data.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.logging.DocumentLog;
import dk.ledocsystem.data.model.logging.QDocumentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

public interface DocumentLogRepository extends JpaRepository<DocumentLog, Long>,
        QuerydslPredicateExecutor<DocumentLog>, QuerydslBinderCustomizer<QDocumentLog> {

    @Override
    default void customize(QuerydslBindings bindings, QDocumentLog root) {
        bindings.including(root.employee.id, root.logType);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
