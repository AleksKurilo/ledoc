package dk.ledocsystem.service.api;

import dk.ledocsystem.service.impl.excel.model.ModuleDTO;

public interface ExcelExportService {

    byte[] export(ModuleDTO module);
}
