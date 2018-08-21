package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Location;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @EntityGraph(attributePaths = "address")
    @Override
    List<Location> findAll();

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
