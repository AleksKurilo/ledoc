package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>  {

    /**
     * @return All {@link Customer} customers that are not archived
     */
    List<Customer> findAllByArchivedIsFalse();

    /**
     * @return All {@link Customer} customers that are archived
     */
    List<Customer> findAllByArchivedIsTrue();

    /**
     * @param cvr company's cvr code
     * @return Customer {@link Customer} with the given cvr code
     */
    Customer findCustomerByCvrEquals(String cvr);

    /**
     * @param name company's name
     * @return Customer {@link Customer} with the given name
     */
    Customer findCustomerByNameEquals(String name);

    /**
     * @param phone company phone
     * @return Customer {@link Customer} with the given phone
     */
    Customer findCustomerByContactPhoneEquals(String phone);

}
