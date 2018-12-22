package dk.ledocsystem.service.impl.excel.sheets;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Row {
    private List<String> values = new ArrayList<>();
}
