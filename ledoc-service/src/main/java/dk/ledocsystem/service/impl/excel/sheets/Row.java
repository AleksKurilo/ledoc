package dk.ledocsystem.service.impl.excel.sheets;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Row {
    private List<String> values = new ArrayList<>();
}
