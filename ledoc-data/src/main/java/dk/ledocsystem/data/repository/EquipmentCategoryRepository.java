package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.equipment.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);

}
