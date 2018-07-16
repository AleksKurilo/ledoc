package dk.ledocsystem.ledoc.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class Supplier {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;

    private String contactPhone;

    private String contactEmail;

    private Long categoryId;

    private Long responsibleId;

    private Boolean archived;

    private String archiveReason;
}
