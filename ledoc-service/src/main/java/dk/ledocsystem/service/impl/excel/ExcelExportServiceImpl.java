package dk.ledocsystem.service.impl.excel;

import dk.ledocsystem.service.api.ExcelExportService;
import dk.ledocsystem.service.impl.excel.sheets.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class ExcelExportServiceImpl implements ExcelExportService {
    private static final String HEADER_CENTER_FORMAT_STRING = "     %s     ";

    @Override
    public Workbook exportSheets(List<EntitySheet> sheets) {
        Workbook workbook = new XSSFWorkbook();
        fillEntitySheets(workbook, sheets);
        return workbook;
    }

    private void fillEntitySheets(Workbook workbook, List<EntitySheet> sheets) {
        sheets.forEach(sheet -> fillEntitySheet(workbook, sheet));
    }

    private void fillEntitySheet(Workbook workbook, EntitySheet sheet) {
        Sheet excelSheet = workbook.createSheet(sheet.getName());
        excelSheet.setPrintGridlines(true);
        excelSheet.setDisplayGridlines(true);

        fillHeaders(excelSheet, sheet.getHeaders(), getHeaderStyle(workbook));
        autoSizeColumns(excelSheet, sheet.getHeaders().size());
        fillRows(excelSheet, sheet.getRows(), getRegularStyle(workbook));
    }

    private void autoSizeColumns(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void fillHeaders(Sheet sheet, List<String> headers, CellStyle headerStyle) {
        Row row = sheet.createRow(0);
        for (int i = 0, size = headers.size(); i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(String.format(HEADER_CENTER_FORMAT_STRING, headers.get(i)));
            cell.setCellStyle(headerStyle);
        }
        row.setHeight((short) 600);
        sheet.createFreezePane(0, 1);
    }

    private void fillRows(Sheet sheet, List<dk.ledocsystem.service.impl.excel.sheets.Row> rows, CellStyle cellStyle) {
        for (int i = 0, size = rows.size(); i < size; i++) {
            fillRow(sheet.createRow(i + 1), rows.get(i).getValues(), cellStyle);
        }
    }

    private void fillRow(Row row, List<String> values, CellStyle cellStyle) {
        for (int i = 0, size = values.size(); i < size; i++) {
            Cell cell = row.createCell(i);
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
