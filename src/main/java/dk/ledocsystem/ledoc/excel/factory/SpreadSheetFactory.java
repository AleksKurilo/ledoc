package dk.ledocsystem.ledoc.excel.factory;

import dk.ledocsystem.ledoc.excel.model.ModuleDTO;
import dk.ledocsystem.ledoc.excel.model.Sheet;
import dk.ledocsystem.ledoc.exceptions.LedocException;
import org.pmw.tinylog.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpreadSheetFactory {

    private static final String CLASSES_PACKAGE = "dk.ledocsystem.ledoc.excel.model.";
    private static final String CLASSES_SUFFIX = "Sheet";

    public List<Sheet> getSheets(ModuleDTO module) {
        return getInplementations(module);
    }

    private List<Sheet> getInplementations(ModuleDTO moduleDTO) {
        List<Sheet> result = new ArrayList<>(moduleDTO.getTables().length);
        Arrays.stream(moduleDTO.getTables()).forEach(t -> {
            result.add(loadImpl(moduleDTO.getModule(), t));
        });

        return result;
    }

    @SuppressWarnings("unchecked")
    private Sheet loadImpl(String moduleName, String tableName) {
        String className = getFullClassName(moduleName, tableName);
        try {
            Class<Sheet> impl = (Class<Sheet>)Class.forName(className);
            return impl.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error(e);
            throw new LedocException("dashboard.excel.error");
        }
    }

    private String getFullClassName(String module, String tableName) {
        return CLASSES_PACKAGE + module +
                "." +
                tableName +
                CLASSES_SUFFIX;
    }

}
