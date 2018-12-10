package dk.ledocsystem.service.api;

import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import dk.ledocsystem.service.impl.excel.model.ExportRequest;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ExcelExportService {

    Workbook export(ExportRequest module);

    Workbook exportSheet(Sheet sheet);

    Workbook exportWorkbook(List<EntitySheet> sheets);
}
