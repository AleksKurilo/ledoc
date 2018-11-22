package dk.ledocsystem.service.impl;

import dk.ledocsystem.service.impl.excel.SpreadSheetBuilder;
import dk.ledocsystem.service.impl.excel.SpreadSheetFactory;
import dk.ledocsystem.service.impl.excel.model.ModuleDTO;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import dk.ledocsystem.service.api.exceptions.ExcelExportException;
import dk.ledocsystem.service.api.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pmw.tinylog.Logger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
class ExcelExportServiceImpl implements ExcelExportService {

    private final SpreadSheetBuilder builder;
    private final SpreadSheetFactory factory;

    @Override
    public byte[] export(ModuleDTO module) {
        List<Sheet> sheets = factory.getSheets(module);
        return getBytes(builder.build(sheets));
    }

    private byte[] getBytes(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        }
        catch (IOException e) {
            Logger.error(e);
            throw new ExcelExportException("dashboard.excel.error");
        }
        return bos.toByteArray();
    }
}
