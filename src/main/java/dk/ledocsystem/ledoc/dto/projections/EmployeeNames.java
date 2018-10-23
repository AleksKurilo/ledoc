package dk.ledocsystem.ledoc.dto.projections;

import org.springframework.beans.factory.annotation.Value;

public interface EmployeeNames {
    Long getId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getName();
}
