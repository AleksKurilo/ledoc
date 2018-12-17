package dk.ledocsystem.data.repository;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.document.QDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface DocumentRepository extends JpaRepository<Document, Long>, QuerydslPredicateExecutor<Document>,
        QuerydslBinderCustomizer<QDocument> {

    List<Document> findAll(Predicate predicate);

    Optional<Document> findById(long id);

    Set<Document> findByEmployeeId(long employeeId);

    Set<Document> findByEquipmentId(long equipmentId);

    boolean existsByNameAndCustomerId(String name, Long customerId);

    /**
     * Deletes documents with the given IDs.
     *
     * @param ids The collection of document IDs.
     */
    @Modifying
    @Query("delete from Document d where d.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QDocument root) {
        bindings.including(root.name, root.location.name, root.status,
                root.responsible.id, root.creator.id, root.archived, root.location.id, root.trade.id,
                root.category.id, root.subcategory.id);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
