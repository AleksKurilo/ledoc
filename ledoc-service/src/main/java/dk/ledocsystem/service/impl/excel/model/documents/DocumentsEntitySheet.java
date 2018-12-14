package dk.ledocsystem.service.impl.excel.model.documents;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.DocumentService;
import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class DocumentsEntitySheet implements EntitySheet {

    private final DocumentService documentService;

    private UserDetails currentUserDetails;
    private Predicate predicate;
    private boolean isNew;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "CATEGORY", "SUBCATEGORY", "TRADES", "VERSION", "DUE DATE",
                "LOCATIONS", "RESPONSIBLE");
    }

    @Override
    public List<List<String>> getRows() {
        return documentService.getAllForExport(currentUserDetails, predicate, isNew);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
