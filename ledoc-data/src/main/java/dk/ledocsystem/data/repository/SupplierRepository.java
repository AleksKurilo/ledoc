package dk.ledocsystem.data.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.supplier.QSupplier;
import dk.ledocsystem.data.model.supplier.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long>,
        QuerydslPredicateExecutor<Supplier>,
        QuerydslBinderCustomizer<QSupplier> {

    List<Supplier> findAll(Predicate predicate);

    long countByCustomerId(Long customerId);

    long countByCustomerIdAndArchivedFalse(Long customerId);

    boolean existsByNameAndCustomerId(String name, Long customerId);

    /**
     * Deletes supplier with the given IDs
     *
     * @param ids The collections of suppliers IDs.
     */
    @Modifying
    @Query("delete from Supplier s where s.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QSupplier root) {
        bindings.including(root.name, root.responsible.id, root.reviewResponsible.id,
                root.category.id, root.category.id, root.archived,
                root.review,
                ExpressionUtils.path(Supplier.class, root, "locations.id"));
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
