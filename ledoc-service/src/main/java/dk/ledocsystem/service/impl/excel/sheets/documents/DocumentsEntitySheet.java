package dk.ledocsystem.service.impl.excel.sheets.documents;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentExportDTO;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.Row;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DocumentsEntitySheet implements EntitySheet {

    private final DocumentService documentService;

    private UserDetails currentUserDetails;
    private String searchString;
    private Predicate predicate;
    private boolean isNew;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "CATEGORY", "SUBCATEGORY", "TRADES", "VERSION", "DUE DATE",
                "LOCATIONS", "RESPONSIBLE");
    }

    @Override
    public List<Row> getRows() {
        return documentService.getAllForExport(currentUserDetails, searchString, predicate, isNew)
                .stream()
                .map(DocumentExportDTO::getFields)
                .map(Row::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
