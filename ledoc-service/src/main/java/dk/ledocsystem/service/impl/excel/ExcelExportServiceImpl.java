package dk.ledocsystem.service.impl.excel;

import dk.ledocsystem.service.impl.excel.model.ExportRequest;
import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.impl.excel.model.Row;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import dk.ledocsystem.service.impl.excel.model.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ExcelExportServiceImpl implements ExcelExportService {

    private final RowMapper rowMapper;

    @Override
    public Workbook export(ExportRequest exportRequest) {
        List<Sheet> sheets = exportRequest.getTables().stream().map(Table::newSheet).collect(Collectors.toList());
        Workbook workbook = new XSSFWorkbook();

        fillSheets(workbook, sheets);
        return workbook;
    }

    private void fillSheets(Workbook workbook, List<Sheet> sheets) {
        sheets.forEach(sheet -> fillSheet(workbook, sheet));
    }

    @Override
    public Workbook exportSheet(Sheet sheet) {
        Workbook workbook = new XSSFWorkbook();
        fillSheet(workbook, sheet);
        return workbook;
    }

    private void fillSheet(Workbook workbook, Sheet sheet) {
        org.apache.poi.ss.usermodel.Sheet excelSheet = workbook.createSheet(sheet.getName());
        List<Row> rows = rowMapper.mapRows(sheet);

        fillHeaders(excelSheet, sheet.getHeaders());
        fillRows(excelSheet, rows);
    }

    private void fillHeaders(org.apache.poi.ss.usermodel.Sheet sheet, List<String> headers) {
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
        for (int i = 0, size = headers.size(); i < size; i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }
    }

    private void fillRows(org.apache.poi.ss.usermodel.Sheet sheet, List<Row> rows) {
        for (int i = 0, size = rows.size(); i < size; i++) {
            fillRow(sheet.createRow(i + 1), rows.get(i));
        }
    }

    private void fillRow(org.apache.poi.ss.usermodel.Row excelRow, Row row) {
        List<String> values = row.getValues();
        for (int i = 0, size = values.size(); i < size; i++) {
            excelRow.createCell(i).setCellValue(values.get(i));
        }
    }

}
