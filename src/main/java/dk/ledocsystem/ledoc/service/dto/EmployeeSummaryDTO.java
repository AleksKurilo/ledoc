package dk.ledocsystem.ledoc.service.dto;

import dk.ledocsystem.ledoc.dto.projections.EmployeeSummary;
import lombok.Getter;

import java.util.Collection;

@Getter
public class EmployeeSummaryDTO {
    private final Long id;

    private final String name;

    private final Collection<Long> locations;

    public EmployeeSummaryDTO(EmployeeSummary employeeSummary) {
        this.id = employeeSummary.getId();
        this.name = employeeSummary.getName();
        this.locations = employeeSummary.getLocations();
    }
}
