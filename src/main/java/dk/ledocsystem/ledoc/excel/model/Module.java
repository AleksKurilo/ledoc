package dk.ledocsystem.ledoc.excel.model;

import dk.ledocsystem.ledoc.excel.exception.InvalidModuleException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Module {

    private static final Map<String, String[]> MODULES = new HashMap<>();

    /*For further modules add module name if not already exists, then add table name. NOTE that table name should be
    * same as class that implements dk.ledocsystem.ledoc.excel.model.Sheet interface except "Sheet" suffix. For example: if
    * we want to have archived equipments then add to map key "equipments" and "ArchivedEquipments" to string array. Define
    * package dk.ledocsystem.ledoc.excel.model.equipments and class named ArchivedEquipmentsSheet implemented Sheet.*/

    static {
        MODULES.put("locations", new String[]{"Locations", "ArchivedLocations"});
        MODULES.put("employees", new String[]{"MyEmployees", "CompanyEmployees", "ArchivedEmployees"});
        MODULES.put("equipments", new String[]{"MyEquipment", "CompanyEquipment", "LoanedEquipment", "BorrowedEquipment"});
    }

    public static void validate(String module, String... tables) {
        String[] tableNames = MODULES.get(module);
        if (tableNames != null) {
            if (!Arrays.asList(tableNames).containsAll(Arrays.asList(tables))) {
                throw new InvalidModuleException("excel.module.invalid", module);
            }
        }
        else {
            throw new InvalidModuleException("excel.module.invalid", module);
        }
    }
}
