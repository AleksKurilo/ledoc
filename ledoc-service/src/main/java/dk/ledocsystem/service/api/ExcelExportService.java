package dk.ledocsystem.service.api;

import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ExcelExportService {

    Workbook exportSheets(List<EntitySheet> sheets);
}
