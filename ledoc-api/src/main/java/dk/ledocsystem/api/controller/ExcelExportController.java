package dk.ledocsystem.api.controller;

import dk.ledocsystem.service.impl.excel.model.ExportRequest;
import dk.ledocsystem.service.api.ExcelExportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;

@RestController
@AllArgsConstructor
@RequestMapping("/excel")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    @RolesAllowed({"admin", "user"})
    @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> export(@RequestBody ExportRequest exportRequest) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + getExportFileName(exportRequest))
                .body(streamBody(exportRequest));
    }

    private StreamingResponseBody streamBody(ExportRequest exportRequest) {
        return outputStream -> excelExportService.export(exportRequest).write(outputStream);
    }

    private String getExportFileName(ExportRequest exportRequest) {
        return "\"" + exportRequest.getModule().toString().toLowerCase() + ".xlsx\"";
    }

}
