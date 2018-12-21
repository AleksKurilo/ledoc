package dk.ledocsystem.service.impl.excel;

import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.impl.excel.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ExcelExportServiceImpl implements ExcelExportService {
    private static final String HEADER_CENTER_FORMAT_STRING = "     %s     ";

    private final RowMapper rowMapper;

    @Override
    public Workbook export(ExportRequest exportRequest) {
        List<Sheet> sheets = exportRequest.getTables().stream().map(Table::newSheet).collect(Collectors.toList());
        Workbook workbook = new XSSFWorkbook();

        fillSheets(workbook, sheets);
        return workbook;
    }

    @Override
    public Workbook exportSheet(Sheet sheet) {
        Workbook workbook = new XSSFWorkbook();
        fillSheet(workbook, sheet);
        return workbook;
    }

    @Override
    public Workbook exportWorkbook(List<EntitySheet> sheets) {
        Workbook workbook = new XSSFWorkbook();
        fillEntitySheets(workbook, sheets);
        return workbook;
    }

    private void fillSheets(Workbook workbook, List<Sheet> sheets) {
        sheets.forEach(sheet -> fillSheet(workbook, sheet));
    }

    private void fillEntitySheets(Workbook workbook, List<EntitySheet> sheets) {
        sheets.forEach(sheet -> fillEntitySheet(workbook, sheet));
    }

    private void fillSheet(Workbook workbook, Sheet sheet) {
        org.apache.poi.ss.usermodel.Sheet excelSheet = workbook.createSheet(sheet.getName());
        excelSheet.setPrintGridlines(true);
        excelSheet.setDisplayGridlines(true);

        List<Row> rows = rowMapper.mapRows(sheet);
        fillHeaders(excelSheet, sheet.getHeaders(), getHeaderStyle(workbook));
        autoSizeColumns(excelSheet, sheet.getHeaders().size());
        fillRows(excelSheet, rows, getRegularStyle(workbook));
    }

    private void fillEntitySheet(Workbook workbook, EntitySheet sheet) {
        org.apache.poi.ss.usermodel.Sheet excelSheet = workbook.createSheet(sheet.getName());
        excelSheet.setPrintGridlines(true);
        excelSheet.setDisplayGridlines(true);

        fillHeaders(excelSheet, sheet.getHeaders(), getHeaderStyle(workbook));
        autoSizeColumns(excelSheet, sheet.getHeaders().size());
        fillRowsFromEntity(excelSheet, sheet.getRows(), getRegularStyle(workbook));
    }

    private void autoSizeColumns(org.apache.poi.ss.usermodel.Sheet excelSheet, int columns) {
        for (int i = 0; i < columns; i++) {
            excelSheet.autoSizeColumn(i);
        }
    }

    private void fillHeaders(org.apache.poi.ss.usermodel.Sheet sheet, List<String> headers, CellStyle headerStyle) {
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
        for (int i = 0, size = headers.size(); i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(String.format(HEADER_CENTER_FORMAT_STRING, headers.get(i)));
            cell.setCellStyle(headerStyle);
        }
        row.setHeight((short) 600);
        sheet.createFreezePane(0, 1);
    }

    private void fillRows(org.apache.poi.ss.usermodel.Sheet sheet, List<Row> rows, CellStyle cellStyle) {
        for (int i = 0, size = rows.size(); i < size; i++) {
            fillRow(sheet.createRow(i + 1), rows.get(i).getValues(), cellStyle);
        }
    }

    private void fillRowsFromEntity(org.apache.poi.ss.usermodel.Sheet sheet, List<List<String>> rows, CellStyle cellStyle) {
        for (int i = 0, size = rows.size(); i < size; i++) {
            fillRow(sheet.createRow(i + 1), rows.get(i), cellStyle);
        }
    }

    private void fillRow(org.apache.poi.ss.usermodel.Row excelRow, List<String> values, CellStyle cellStyle) {
        for (int i = 0, size = values.size(); i < size; i++) {
            Cell cell = excelRow.createCell(i);
            cell.setCellValue(values.get(i));
            cell.setCellStyle(cellStyle);
        }
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.index);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private CellStyle getRegularStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        return cellStyle;
    }
}
