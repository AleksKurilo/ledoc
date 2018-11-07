package dk.ledocsystem.ledoc.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.equipment.QEquipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, LoggingRepository<Equipment, Long>,
        QuerydslPredicateExecutor<Equipment>, QuerydslBinderCustomizer<QEquipment> {

    @EntityGraph(attributePaths = {"loan", "location.name"})
    @Override
    Page<Equipment> findAll(Predicate predicate, Pageable pageable);

    @EntityGraph(attributePaths = {"loan", "responsible"})
    List<Equipment> findAllByArchivedFalseAndNextReviewDateNotNull();

    boolean existsByNameAndCustomerId(String name, Long customerId);

    /**
     * Deletes equipment items with the given IDs.
     *
     * @param ids The collection of equipment IDs.
     */
    @Modifying
    @Query("delete from Equipment eq where eq.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QEquipment root) {
        bindings.including(root.name, root.localId, root.location.name, root.status,
                root.responsible.id, root.archived, root.location.id,
                root.category.id, root.authenticationType.id, root.loan.borrower.id);
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
