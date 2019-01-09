package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.review.Module;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ReviewTemplateRepository extends JpaRepository<ReviewTemplate, Long>,
        QuerydslPredicateExecutor<ReviewTemplate> {

    Optional<ReviewTemplate> findByNameAndCustomer(String name, Customer customer);

    Optional<ReviewTemplate> findByName(String name);

    long countByCustomerId(Long customerId);

    long countByCustomerIdAndModule(Long customerId, Module module);

    /**
     * Deletes review templates with the given IDs.
     *
     * @param ids The collection of review template IDs.
     */
    @Modifying
    @Query("delete from ReviewTemplate rt where rt.id in ?1")
    void deleteByIdIn(Iterable<Long> ids);
}
