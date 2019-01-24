package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.QCustomer;
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

    /**
     * @return {@code true} if there is user provided name
     */
    boolean existsByName(String name);

    /**
     * @return {@code true} if there is user provided cvr
     */
    boolean existsByCvr(String cvr);

    @Modifying
    @Query("delete from Customer c where c.id in ?1")
    void deleteById(Iterable<Long> ids);

    @Override
    default void customize(QuerydslBindings bindings, QCustomer root) {
        bindings.including(root.pointOfContact.id);
    }
}
