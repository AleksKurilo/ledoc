package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierCategoryDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;

import java.util.List;

public interface SupplierCategoryService {

    IdAndLocalizedName create(SupplierCategoryDTO category);

    IdAndLocalizedName update(long id, SupplierCategoryDTO category);

    List<IdAndLocalizedName> getList();

    void delete(Long id);
}
