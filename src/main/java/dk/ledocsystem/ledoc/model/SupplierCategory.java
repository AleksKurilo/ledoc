package dk.ledocsystem.ledoc.model;

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
