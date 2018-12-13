package dk.ledocsystem.service.impl.excel.model.equipment;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.EquipmentService;
import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class EquipmentEntitySheet implements EntitySheet {

    private final EquipmentService equipmentService;

    private UserDetails currentUserDetails;
    private Predicate predicate;
    private boolean isNew;
    private String name;

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "CATEGORY", "ID NUMBER", "SERIAL NUMBER", "HOME LOCATION", "CURRENT LOCATION",
                "REVIEW RESPONSIBLE", "LOAN STATUS", "STATUS", "DUE DATE", "SUPPLIER", "REVIEW STATUS",
                "MUST BE REVIEWED", "AUTHENTICATION TYPE", "RESPONSIBLE", "LOCAL ID");
    }

    @Override
    public List<List<String>> getRows() {
        return equipmentService.getAllForExport(currentUserDetails, predicate, isNew);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
