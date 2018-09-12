package dk.ledocsystem.ledoc.excel.service;

import dk.ledocsystem.ledoc.excel.model.Row;
import dk.ledocsystem.ledoc.excel.model.Sheet;
import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class SpreadSheetBuilder {

    private final QueryExecutor queryExecutor;

    XSSFWorkbook build(List<Sheet> sheets) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        init(sheets, workbook);
        return workbook;
    }

    private void init(List<Sheet> sheets, XSSFWorkbook workbook) {
        sheets.forEach(sheet -> {
            buildExcelSheet(sheet, workbook);
        });
    }

    private void buildExcelSheet(Sheet sheet, XSSFWorkbook workbook) {
        XSSFSheet xssfSheet = workbook.createSheet(sheet.getName());
        List<Row> rows = queryExecutor.mapRows(sheet.getQuery());

        fillInHeader(sheet.getHeaders(), xssfSheet);
        fillInRows(rows, xssfSheet);
    }

    private void fillInHeader(List<String> headers, XSSFSheet xssfSheet) {
        XSSFRow row = xssfSheet.createRow(0);
        IntStream.range(0, headers.size()).forEach(i -> {
            row.createCell(i).setCellValue(headers.get(i));
        });
    }

    private void fillInRows(List<Row> rows, XSSFSheet xssfSheet) {
        IntStream.range(1, rows.size()+1).forEach(i -> {
            buildCells(xssfSheet.createRow(i), rows.get(i-1));
        });
    }

    private void buildCells(XSSFRow xssfRow, Row row) {
        IntStream.range(0, row.getValues().size()).forEach(i -> {
            xssfRow.createCell(i).setCellValue(row.getValues().get(i).toString());
        });
    }
}
