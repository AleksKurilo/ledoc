package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>  {

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
     * @return All {@link Customer} customers that are not archived
     */
    Page<Customer> findAllByArchivedIsFalse(Pageable pageable);

    /**
     * @return Count of {@link Customer} customers that are not archived
     */
    Integer countAllByArchivedFalse();

    /**
     * @return All {@link Customer} customers that are archived
     */
    List<Customer> findAllByArchivedIsTrue();

    @Modifying
    @Query("delete from Customer c where c.id in ?1")
    void deleteByIdIn(Collection<Long> ids);

    /**
     * @param phone company phone
     * @return Customer {@link Customer} with the given phone
     */
    Optional<Customer> findByContactPhone(String phone);

    /**
     * @param contactEmail contactEmail
     * @return Customer {@link Customer} with the given contact email
     */
    Optional<Customer> findByContactEmail(String contactEmail);

    /**
     * @param invoiceEmail invoiceEmail
     * @return Customer {@link Customer} with the given invoice email
     */
    Optional<Customer> findByInvoiceEmail(String invoiceEmail);

    /**
     * @param companyEmail companyEmail
     * @return Customer {@link Customer} with the company email
     */
    Optional<Customer> findByCompanyEmail(String companyEmail);
}
