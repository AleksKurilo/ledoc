package dk.ledocsystem.service.impl.excel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import dk.ledocsystem.service.api.exceptions.InvalidModuleException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Module {

    LOCATIONS {
        @Override
        public List<Table> getTables() {
            return Arrays.asList(Table.LOCATIONS, Table.ARCHIVED_LOCATIONS);
        }
    },
    EMPLOYEES {
        @Override
        public List<Table> getTables() {
            return Collections.singletonList(Table.EMPLOYEES);
        }
    },
    EQUIPMENT {
        @Override
        public List<Table> getTables() {
            return Arrays.asList(Table.MY_EQUIPMENT, Table.BORROWED_EQUIPMENT, Table.ARCHIVED_EQUIPMENT);
        }
    };

    public abstract List<Table> getTables();

    @JsonCreator
    public static Module fromString(String value) {
        for (Module module : values()) {
            if (module.toString().equalsIgnoreCase(value)) {
                return module;
            }
        }
        throw new InvalidModuleException("excel.module.invalid", value);
    }
}
