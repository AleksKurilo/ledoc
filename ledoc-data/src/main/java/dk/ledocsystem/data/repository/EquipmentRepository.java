package dk.ledocsystem.data.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.equipment.QEquipment;
import dk.ledocsystem.data.util.LocalDateMultiValueBinding;
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

import java.time.LocalDate;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, QuerydslPredicateExecutor<Equipment>,
        QuerydslBinderCustomizer<QEquipment> {

    @EntityGraph(attributePaths = {"loan"})
    @Override
    Page<Equipment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"loan"})
    @Override
    Page<Equipment> findAll(Predicate predicate, Pageable pageable);

    List<Equipment> findAll(Predicate predicate);

    @Query("select e from Equipment e join fetch e.responsible left join fetch e.loan " +
            "where e.nextReviewDate is not null and e.archived = false")
    List<Equipment> findAllForReview();

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
        bindings.including(root.name, root.localId, root.location.id, root.location.name, root.status, root.readyToLoan,
                root.idNumber, root.serialNumber, root.manufacturer, root.purchaseDate, root.warrantyDate,
                root.price, root.comment, root.responsible.id, root.creator.id, root.archived, root.category.id,
                root.category.nameEn, root.authenticationType.id, root.authenticationType.nameEn, root.loan.borrower.id,
                ExpressionUtils.path(String.class, root, "responsible.name"));
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.bind(ExpressionUtils.path(String.class, root.responsible, "name"))
                .first((path, val) -> root.responsible.firstName.concat(" ").concat(root.responsible.lastName).containsIgnoreCase(val));
        bindings.bind(LocalDate.class).all(new LocalDateMultiValueBinding());
    }
}
