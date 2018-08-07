package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.model.employee.Employee;

import java.util.Set;

/**
 * All implementations are subjects to visiting by {@link dk.ledocsystem.ledoc.model.employee.Employee system users}.
 *
 * @see dk.ledocsystem.ledoc.repository.LoggingRepository
 */
public interface Visitable {

    Set<Employee> getVisitedBy();
}
