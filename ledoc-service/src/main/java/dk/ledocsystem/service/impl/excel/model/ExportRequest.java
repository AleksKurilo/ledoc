package dk.ledocsystem.service.impl.excel.model;

import lombok.Data;

import java.util.Set;

@Data
@dk.ledocsystem.service.api.validation.excel.ExportRequest
public class ExportRequest {

    private Module module;

    private Set<Table> tables;
}
