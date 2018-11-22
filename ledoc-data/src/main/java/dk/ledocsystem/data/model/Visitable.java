package dk.ledocsystem.data.model;

import dk.ledocsystem.data.model.employee.Employee;

import java.util.Set;

/**
 * All implementations are subjects to visiting by {@link dk.ledocsystem.data.model.employee.Employee system users}.
 *
 * @see dk.ledocsystem.data.repository.LoggingRepository
 */
public interface Visitable {

    Set<Employee> getVisitedBy();
}
