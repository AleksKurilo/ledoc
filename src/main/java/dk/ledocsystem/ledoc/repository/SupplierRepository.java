package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * @return All {@link Supplier} suppliers that are not archived
     */
    List<Supplier> findAllByArchivedIsFalse();

    /**
     * @return All {@link Supplier} suppliers that are archived
     */
    List<Supplier> findAllByArchivedIsTrue();

    /**
     * @param supplierCategoryId supplierCategoryId
     * @return All {@link Supplier} suppliers associated with given category
     */
    List<Supplier> findAllByCategory_Id(Long supplierCategoryId);

    /**
     * @param supplierCategoryId supplierCategoryId
     * @return All {@link Supplier} archived suppliers associated with given category
     */
    List<Supplier> findAllByCategory_IdAndArchivedIsTrue(Long supplierCategoryId);

    /**
     * @param supplierCategoryId supplierCategoryId
     * @return All {@link Supplier} not archived suppliers associated with given category
     */
    List<Supplier> findAllByCategory_IdAndArchivedIsFalse(Long supplierCategoryId);

    /**
     * @param responsibleId responsibleId
     * @return All {@link Supplier} suppliers associated with given responsible employee
     */
    List<Supplier> findAllByEmployee_Id(Long responsibleId);

    /**
     * @param responsibleId responsibleId
     * @return All {@link Supplier} archived suppliers associated with given responsible employee
     */
    List<Supplier> findAllByEmployee_IdAndArchivedIsTrue(Long responsibleId);

    /**
     * @param responsibleId responsibleId
     * @return All {@link Supplier} not archived suppliers associated with given responsible employee
     */
    List<Supplier> findAllByEmployee_IdAndArchivedIsFalse(Long responsibleId);

    /**
     * @param customerId customerId
     * @return All {@link Supplier} suppliers associated with given responsible employee
     */
    List<Supplier> findAllByCustomer_Id(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Supplier} archived suppliers associated with given responsible employee
     */
    List<Supplier> findAllByCustomer_IdAndArchivedIsTrue(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Supplier} not archived suppliers associated with given responsible employee
     */
    List<Supplier> findAllByCustomer_IdAndArchivedIsFalse(Long customerId);

}
