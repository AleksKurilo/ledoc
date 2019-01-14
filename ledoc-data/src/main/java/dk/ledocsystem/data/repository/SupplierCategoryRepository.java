package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.supplier.SupplierCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);
}
