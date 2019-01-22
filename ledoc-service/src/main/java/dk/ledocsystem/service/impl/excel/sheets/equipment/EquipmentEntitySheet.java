package dk.ledocsystem.service.impl.excel.sheets.equipment;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.service.api.EquipmentService;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentExportDTO;
import dk.ledocsystem.service.impl.excel.sheets.EntitySheet;
import dk.ledocsystem.service.impl.excel.sheets.Row;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class EquipmentEntitySheet implements EntitySheet {

    private final EquipmentService equipmentService;

    private UserDetails currentUserDetails;
    private String searchString;
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
    public List<Row> getRows() {
        return equipmentService.getAllForExport(currentUserDetails, searchString, predicate, isNew)
                .stream()
                .map(EquipmentExportDTO::getFields)
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
