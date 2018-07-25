package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Assigns the admin authorities to the {@link Employee} with the given ID.
     *
     * @param employeeId ID of the {@link Employee}
     */
    @Procedure("set_admin_authorities")
    void setAdminAuthorities(@Param("employee_id") Long employeeId);

    /**
     * @param email Email
     * @return {@link Optional} with {@link Employee employee} with provided email or empty Optional if none found.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Deletes employees with the given IDs.
     *
     * @param ids The collection of employee IDs.
     */
    @Modifying
    @Query(value = "delete from main.employees e where e.id in ?1", nativeQuery = true)
    void deleteByIdIn(Collection<Long> ids);

    /**
     * @return All {@link Employee} employees that are not archived
     */
    List<Employee> findAllByArchivedIsFalse();

    /**
     * @return All {@link Employee} employees that are archived
     */
    List<Employee> findAllByArchivedIsTrue();

    /**
     * @param customerId customerId
     * @return All {@link Employee} eployees of current {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_Id(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Employee} not archived eployees of current {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_IdAndArchivedIsFalse(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Employee} archived in given {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_IdAndArchivedIsTrue(Long customerId);

    /**
     * @param locationID locationID
     * @param includeArchived includeArchived if we want get archived eployees too
     * @return All {@link Employee} eployees of given {@link dk.ledocsystem.ledoc.model.Location} location
     *//*
    List<Employee> allbyLocation(Long );

    *//**
     * @param locationID locationID
     * @return All {@link Employee} archived eployees of given {@link dk.ledocsystem.ledoc.model.Location} location
     *//*
    List<Employee> allbyLocationarchived(Long );

    *//**
     * @param addressId addressId
     * @param includeArchived includeArchived if we want get archived eployees too
     * @return All {@link Employee} employees of given {@link dk.ledocsystem.ledoc.model.Address} address
     *//*
    List<Employee> findAllInAddress(Long );

    *//**
     * @param addressId addressId
     * @return All {@link Employee} archived employees of given {@link dk.ledocsystem.ledoc.model.Address} address
     *//*
    List<Employee> findAllArchivedInAddress(Long );*/
}
