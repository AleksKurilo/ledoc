package dk.ledocsystem.service.api.dto.inbound.supplier;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.supplier.UniqueSupplierCategoryNameDa;
import dk.ledocsystem.service.api.validation.supplier.UniqueSupplierCategoryNameEn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierCategoryDTO {

    @NonCyrillic
    @UniqueSupplierCategoryNameEn
    private String nameEn;

    @NonCyrillic
    @UniqueSupplierCategoryNameDa
    private String nameDa;

    private String description;
}
