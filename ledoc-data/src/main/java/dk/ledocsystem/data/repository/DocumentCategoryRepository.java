package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.document.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long>, QuerydslPredicateExecutor<DocumentCategory> {
}
