package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Equipment;
import dk.ledocsystem.ledoc.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /**
     * @return All {@link Equipment} equipments that are not archived
     */
    List<Equipment> findAllByArchivedIsFalse();

    /**
     * @return All {@link Equipment} equipments that are not archived
     */
    List<Equipment> findAllByArchivedIsTrue();

    /**
     * @param supplierId supplierId
     * @return All {@link Equipment} associated with given supplier
     */
    List<Equipment> findAllBySupplier_Id(Long supplierId);

    /**
     * @param supplierId supplierId
     * @return All {@link Equipment} archived equipments associated with given supplier
     */
    List<Equipment> findAllBySupplier_IdAndArchivedIsTrue(Long supplierId);

    /**
     * @param supplierId supplierId
     * @return All {@link Equipment} not archived equipments associated with given supplier
     */
    List<Equipment> findAllBySupplier_IdAndArchivedIsFalse(Long supplierId);

    /**
     * @param status status
     * @return All {@link Equipment} equipments associated with given status
     */
    List<Equipment> findAllByStatus(Status status);

    /**
     * @param status status
     * @return All {@link Equipment} archived equipments associated with given status
     */
    List<Equipment> findAllByStatusAndArchivedIsTrue(Status status);

    /**
     * @param status status
     * @return All {@link Equipment} not archived equipments associated with given status
     */
    List<Equipment> findAllByStatusAndArchivedIsFalse(Status status);

    /**
     * @param employeeId employeeId
     * @return All {@link Equipment} equipments owned by given employee
     */
    List<Equipment> findAllByCreator_Id(Long employeeId);

    /**
     * @param employeeId employeeId
     * @return All {@link Equipment} archived equipments owned by given employee
     */
    List<Equipment> findAllByCreator_IdAndArchivedIsTrue(Long employeeId);

    /**
     * @param employeeId employeeId
     * @return All {@link Equipment} not archived equipments owned by given employee
     */
    List<Equipment> findAllByCreator_IdAndArchivedIsFalse(Long employeeId);

    /**
     * @param customerId customerId
     * @return All {@link Equipment} equipments owned by given company
     */
    List<Equipment> findAllByCustomer_Id(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Equipment} archived equipments owned by given company
     */
    List<Equipment> findAllByCustomer_IdAndArchivedIsTrue(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Equipment} not archived equipments owned by given company
     */
    List<Equipment> findAllByCustomer_IdAndArchivedIsFalse(Long customerId);

}
