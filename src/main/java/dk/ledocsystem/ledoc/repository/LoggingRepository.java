package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Visitable;
import dk.ledocsystem.ledoc.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface LoggingRepository<T extends Visitable, ID> extends PagingAndSortingRepository<T, ID> {

    /**
     * Adds the record to visited log.
     *
     * @param employeeId      ID of the {@link Employee} that visited some entity
     * @param visitedEntityId ID of the entity that was visited
     */
    @Modifying
    @Query(value = "INSERT INTO main.#{#entityName}_log VALUES(?1, ?2) ON CONFLICT DO NOTHING", nativeQuery = true)
    void writeToVisitedLog(ID employeeId, ID visitedEntityId);

    /**
     * Finds entities that were not visited (i.e. "new" entities) by {@link Employee}.
     *
     * @param customerId ID of the current customer
     * @param employeeId ID of the employee
     * @param pageable   {@link Pageable}
     * @return Number of visited entities
     */
    @Query(value = "SELECT ent FROM #{#entityName} ent " +
            "where ent.archived = FALSE AND " +
            "ent.id <> ?2 AND " +
            "ent.customer.id = ?1 AND " +
            "ent.id not in (select visited FROM #{#entityName} visited join visited.visitedBy emp WHERE emp.id = ?2)")
    Page<T> getNotVisited(Long customerId, Long employeeId, Pageable pageable);

    /**
     * Counts the number of entities that were visited by {@link Employee}.
     *
     * @param employeeId ID of the employee
     * @return Number of visited entities
     */
    @Query(value = "SELECT COUNT(ent) " +
            "FROM #{#entityName} ent join ent.visitedBy emp where emp.id = ?1 and ent.archived = FALSE")
    long countVisited(ID employeeId);
}
