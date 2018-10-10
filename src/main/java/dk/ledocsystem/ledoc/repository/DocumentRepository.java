package dk.ledocsystem.ledoc.repository;


import dk.ledocsystem.ledoc.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;


public interface DocumentRepository extends JpaRepository<Document, Long>, QuerydslPredicateExecutor<Document> {

    Optional<Document> findById(long id);

    Set<Document> findByEmployeeId(long employeeId);

    Set<Document> findByEquipmentId(long equipmentId);
}
