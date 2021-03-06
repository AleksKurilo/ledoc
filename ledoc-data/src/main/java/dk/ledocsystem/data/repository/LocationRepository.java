package dk.ledocsystem.data.repository;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.projections.LocationSummary;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.QLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface LocationRepository extends JpaRepository<Location, Long>, QuerydslPredicateExecutor<Location>,
        QuerydslBinderCustomizer<QLocation> {

    @EntityGraph(attributePaths = "address")
    @Override
    Page<Location> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "address")
    @Override
    Page<Location> findAll(Predicate predicate, Pageable pageable);

    Page<LocationSummary> findAllByCustomerIdAndArchivedFalse(Long customerId, Pageable pageable);

    /**
     * @return {@code true} if location with given name and customer ID exists.
     */
    boolean existsByNameAndCustomerId(String name, Long customerId);

    /**
     * Deletes locations with the given IDs.
     *
     * @param ids The collection of location IDs.
     */
    @Modifying
    @Query("delete from Location l where l.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QLocation root) {
        bindings.including(root.archived, root.responsible.id, root.addressLocation.id, root.type);
    }
}
