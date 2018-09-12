package dk.ledocsystem.ledoc.excel.service;

import dk.ledocsystem.ledoc.excel.model.Module;
import dk.ledocsystem.ledoc.excel.model.ModuleDTO;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface ExcelExportService {
    StreamingResponseBody export(ModuleDTO module);
}
