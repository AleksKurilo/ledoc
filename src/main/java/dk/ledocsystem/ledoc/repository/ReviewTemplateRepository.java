package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ReviewTemplateRepository extends JpaRepository<ReviewTemplate, Long>,
        QuerydslPredicateExecutor<ReviewTemplate> {

    Optional<ReviewTemplate> findByNameAndCustomer(String name, Customer customer);

    /**
     * Deletes review templates with the given IDs.
     *
     * @param ids The collection of review template IDs.
     */
    @Modifying
    @Query("delete from ReviewTemplate rt where rt.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
