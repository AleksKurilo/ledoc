package dk.ledocsystem.service.impl.excel.model.equipment;

import dk.ledocsystem.service.impl.excel.model.EntitySheet;

import java.util.Arrays;
import java.util.List;


public abstract class AbstractEntityEquipmentSheet implements EntitySheet {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList("NAME", "CATEGORY", "ID NUMBER", "SERIAL NUMBER", "HOME LOCATION", "CURRENT LOCATION",
                "REVIEW RESPONSIBLE", "LOAN STATUS", "STATUS", "DUE DATE", "SUPPLIER", "REVIEW STATUS",
                "MUST BE REVIEWED", "AUTHENTICATION TYPE", "RESPONSIBLE", "LOCAL ID");
    }

    @Override
    public String getName() {
        return "Equipment";
    }
}
