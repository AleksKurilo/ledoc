package dk.ledocsystem.service.api;

import dk.ledocsystem.service.impl.excel.model.ExportRequest;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelExportService {

    Workbook export(ExportRequest module);

    Workbook exportSheet(Sheet sheet);
}
