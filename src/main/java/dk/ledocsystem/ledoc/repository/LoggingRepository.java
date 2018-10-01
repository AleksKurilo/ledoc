package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Visitable;
import dk.ledocsystem.ledoc.model.employee.Employee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface LoggingRepository<T extends Visitable, ID> extends Repository<T, ID> {

    /**
     * Adds the record to visited log.
     *
     * @param employeeId      ID of the {@link Employee} that visited some entity
     * @param visitedEntityId ID of the entity that was visited
     */
    @Modifying
    @Query(value = "INSERT INTO main.#{#entityName}_log VALUES(?1, ?2) ON CONFLICT DO NOTHING", nativeQuery = true)
    void writeToLog(ID employeeId, ID visitedEntityId);
}
