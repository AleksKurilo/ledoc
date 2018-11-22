package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.projections.IdAndLocalizedName;
import dk.ledocsystem.data.model.equipment.EquipmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);

    List<IdAndLocalizedName> getAllBy();

    Page<IdAndLocalizedName> getAllBy(Pageable pageable);
}
