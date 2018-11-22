package dk.ledocsystem.data.projections;

import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

public interface EmployeeSummary {
    Long getId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getName();

    @Value("#{target.locations?:T(java.util.Collections).emptyList()}")
    Collection<Long> getLocations();
}
