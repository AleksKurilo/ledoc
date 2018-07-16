package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
