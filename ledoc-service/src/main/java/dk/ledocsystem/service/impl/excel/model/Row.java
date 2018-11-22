package dk.ledocsystem.service.impl.excel.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Row {
    private List<Object> values = new ArrayList<>();
}
