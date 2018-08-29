package dk.ledocsystem.ledoc.repository;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Collection;

public interface LocationRepository extends JpaRepository<Location, Long>, QuerydslPredicateExecutor<Location> {

    @EntityGraph(attributePaths = "address")
    @Override
    Page<Location> findAll(Predicate predicate, Pageable pageable);

    /**
     * @return {@code true} if location with given name and customer ID exists.
     */
    boolean existsByNameAndCustomerId(String name, Long customerId);

    /**
     * Deletes locations with the given IDs.
     *
     * @param ids The collection of employeeCreateDTO IDs.
     */
    @Modifying
    @Query("delete from Employee e where e.id in ?1")
    void deleteByIdIn(Collection<Long> ids);
}
