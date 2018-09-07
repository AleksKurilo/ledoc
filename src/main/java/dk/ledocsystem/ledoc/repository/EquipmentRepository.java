package dk.ledocsystem.ledoc.repository;

import com.querydsl.core.types.Predicate;
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

import java.util.Collection;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, LoggingRepository<Equipment, Long>,
        QuerydslPredicateExecutor<Equipment>, QuerydslBinderCustomizer<QEquipment> {

    @EntityGraph(attributePaths = "loan")
    @Override
    Page<Equipment> findAll(Predicate predicate, Pageable pageable);

    boolean existsByName(String name);

    /**
     * Deletes equipment items with the given IDs.
     *
     * @param ids The collection of equipment IDs.
     */
    @Modifying
    @Query("delete from Equipment eq where eq.id in ?1")
    void deleteByIdIn(Collection<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QEquipment root) {
        bindings.including(root.responsible.id, root.archived, root.location.id,
                root.category.id, root.authenticationType.id, root.loan.borrower.id);
    }
}
