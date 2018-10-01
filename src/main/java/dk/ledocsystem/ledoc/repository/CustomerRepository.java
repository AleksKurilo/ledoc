package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.QCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, QuerydslPredicateExecutor<Customer>,
        QuerydslBinderCustomizer<QCustomer> {

    /**
     * @param name name
     * @return {@link Optional} with {@link Customer customer} with provided name or empty Optional if none found.
     */
    Optional<Customer> findByName(String name);

    /**
     * @param cvr company's cvr code
     * @return {@link Optional} with {@link Customer customer} with provided CVR or empty Optional if none found.
     */
    Optional<Customer> findByCvr(String cvr);

    /**
     * @return Count of {@link Customer} customers that are not archived
     */
    Integer countAllByArchivedFalse();

    @Modifying
    @Query("delete from Customer c where c.id in ?1")
    void deleteById(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QCustomer root) {
        bindings.including(root.archived, root.pointOfContact.id);
    }
}
