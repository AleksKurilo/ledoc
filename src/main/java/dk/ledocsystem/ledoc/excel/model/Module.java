package dk.ledocsystem.ledoc.excel.model;

import dk.ledocsystem.ledoc.excel.exception.InvalidModuleException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module {

    private static final Map<String, String[]> USER_MODULES = new HashMap<>();
    private static final Map<String, String[]> ADMIN_MODULES = new HashMap<>();

    private static final String ADMIN_ROLE = "ROLE_super_admin";
    private static final String USER_ROLE = "ROLE_user";

    /*For further modules add module name if not already exists, then add table name. NOTE that table name should be
    * same as class that implements dk.ledocsystem.ledoc.excel.model.Sheet interface except "Sheet" suffix. For example: if
    * we want to have archived equipments then add to map key "equipments" and "ArchivedEquipments" to string array. Define
    * package dk.ledocsystem.ledoc.excel.model.equipments and class named ArchivedEquipmentsSheet implemented Sheet.*/

    static {
        USER_MODULES.put("locations", new String[]{"Locations", "ArchivedLocations"});
        USER_MODULES.put("employees", new String[]{"MyEmployees", "CompanyEmployees", "ArchivedEmployees"});
        USER_MODULES.put("equipments", new String[]{"MyEquipment", "LocationEquipment", "LendedEquipment",
                "BorrowedEquipment", "ArchivedEquipment"});
    }

    static {
        ADMIN_MODULES.put("equipmentsA", new String[]{"MyEquipmentAd", "CompanyEquipmentAd",
                "LendedEquipmentAd", "ArchivedEquipmentAd"});
    }

    public static void validate(String module, String... tables) {
        if (SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority(ADMIN_ROLE))) {
            checkModule(ADMIN_MODULES, module, tables);
        }
        else {
            if (SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().contains(new SimpleGrantedAuthority(USER_ROLE))) {
                checkModule(USER_MODULES, module, tables);
            }
            else {
                throw new AccessDeniedException("Access denied for current user");
            }
        }
    }

    private static void checkModule(Map<String, String[]> map, String module, String... tables) {
        String[] tableNames = map.get(module);
        if (tableNames != null) {
            List<String> tablesList = Arrays.asList(tableNames);
            for (String table : tables) {
                if (!tablesList.contains(table)) {
                    throw new InvalidModuleException("excel.table.invalid", table);
                }
            }
        }
        else {
            throw new InvalidModuleException("excel.module.invalid", module);
        }
    }
}
