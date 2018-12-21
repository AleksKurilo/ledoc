package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Data;

import java.util.Set;

@Data
public class EmployeeSummary {

    private Long id;

    private String name;

    private Set<Long> locationIds;
}
