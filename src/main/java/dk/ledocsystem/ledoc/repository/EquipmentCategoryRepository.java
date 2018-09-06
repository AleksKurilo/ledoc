package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);
}
