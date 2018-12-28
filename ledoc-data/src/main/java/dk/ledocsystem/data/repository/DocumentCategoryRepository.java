package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.document.DocumentCategory;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

    boolean existsByNameEn(String nameEn);

    boolean existsByNameDa(String nameDa);

    List<DocumentCategory> findAllByType(DocumentCategoryType type);

}
