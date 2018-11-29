package dk.ledocsystem.service.impl.excel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import dk.ledocsystem.service.api.exceptions.InvalidModuleException;
import dk.ledocsystem.service.impl.excel.model.employees.EmployeesSheet;
import dk.ledocsystem.service.impl.excel.model.equipment.ArchivedEquipmentSheet;
import dk.ledocsystem.service.impl.excel.model.equipment.BorrowedEquipmentSheet;
import dk.ledocsystem.service.impl.excel.model.equipment.MyEquipmentSheet;
import dk.ledocsystem.service.impl.excel.model.locations.ArchivedLocationsSheet;
import dk.ledocsystem.service.impl.excel.model.locations.LocationsSheet;

public enum Table {

    LOCATIONS {
        @Override
        public Sheet newSheet() {
            return new LocationsSheet();
        }
    },
    ARCHIVED_LOCATIONS {
        @Override
        public Sheet newSheet() {
            return new ArchivedLocationsSheet();
        }
    },

    EMPLOYEES {
        @Override
        public Sheet newSheet() {
            return new EmployeesSheet();
        }
    },

    MY_EQUIPMENT {
        @Override
        public Sheet newSheet() {
            return new MyEquipmentSheet();
        }
    },
    BORROWED_EQUIPMENT {
        @Override
        public Sheet newSheet() {
            return new BorrowedEquipmentSheet();
        }
    },
    ARCHIVED_EQUIPMENT {
        @Override
        public Sheet newSheet() {
            return new ArchivedEquipmentSheet();
        }
    };

    public abstract Sheet newSheet();

    @JsonCreator
    public static Table fromString(String value) {
        for (Table table : values()) {
            if (table.toString().equalsIgnoreCase(value)) {
                return table;
            }
        }
        throw new InvalidModuleException("excel.table.invalid", value);
    }
}
