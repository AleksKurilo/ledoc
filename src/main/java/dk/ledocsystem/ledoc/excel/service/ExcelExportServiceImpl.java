package dk.ledocsystem.ledoc.excel.service;

import dk.ledocsystem.ledoc.excel.factory.SpreadSheetFactory;
import dk.ledocsystem.ledoc.excel.model.Module;
import dk.ledocsystem.ledoc.excel.model.ModuleDTO;
import dk.ledocsystem.ledoc.excel.model.Sheet;
import dk.ledocsystem.ledoc.exceptions.ExcelExportException;
import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private final SpreadSheetBuilder builder;
    private final SpreadSheetFactory factory;

    @Override
    public StreamingResponseBody export(ModuleDTO module) {
        List<Sheet> sheets = factory.getSheets(module);
        return writeStream(builder.build(sheets));
    }

    private StreamingResponseBody writeStream(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        }
        catch (IOException e) {
            throw new ExcelExportException("dashboard.excel.error", e.getMessage());
        }

        return outputStream -> outputStream.write(bos.toByteArray());
    }
}
