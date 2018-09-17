package dk.ledocsystem.ledoc.excel.controller;

import dk.ledocsystem.ledoc.excel.model.Module;
import dk.ledocsystem.ledoc.excel.model.ModuleDTO;
import dk.ledocsystem.ledoc.excel.service.ExcelExportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //@RolesAllowed({"admin", "user"})
    @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> export(@RequestBody ModuleDTO moduleDTO) {
        Module.validate(moduleDTO.getModule(), moduleDTO.getTables());
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + moduleDTO.getModule() + "\"")
        /*only for postman tests*/        .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelExportService.export(moduleDTO));
    }


}
