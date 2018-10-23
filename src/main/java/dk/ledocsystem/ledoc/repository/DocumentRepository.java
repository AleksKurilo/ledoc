package dk.ledocsystem.ledoc.repository;


import dk.ledocsystem.ledoc.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;


public interface DocumentRepository extends JpaRepository<Document, Long>, QuerydslPredicateExecutor<Document> {

    Optional<Document> findById(long id);

    Set<Document> findByEmployeeId(long employeeId);

    Set<Document> findByEquipmentId(long equipmentId);

    /**
     * Deletes documents with the given IDs.
     *
     * @param ids The collection of document IDs.
     */
    @Modifying
    @Query("delete from Document d where d.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
