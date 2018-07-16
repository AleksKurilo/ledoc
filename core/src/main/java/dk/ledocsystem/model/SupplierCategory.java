package dk.ledocsystem.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplierCategory {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;
}
