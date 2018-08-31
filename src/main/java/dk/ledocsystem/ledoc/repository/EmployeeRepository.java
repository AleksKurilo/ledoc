package dk.ledocsystem.ledoc.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.*;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.projections.EmployeeDataExcel;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, LoggingRepository<Employee, Long>,
        QuerydslPredicateExecutor<Employee>, QuerydslBinderCustomizer<QEmployee> {

    /**
     * Assigns the provided authorities to {@link Employee} with the given ID.
     *
     * @param employeeId  Employee ID
     * @param authorities Authority object
     */
    @Modifying
    @Query(value = "INSERT INTO main.employee_authorities VALUES(:#{#employeeId}, :#{#authorities.code}) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void addAuthorities(@Param("employeeId") Long employeeId,
                        @Param("authorities") UserAuthorities authorities);

    /**
     * @param authorities {@link UserAuthorities authority}
     * @return All employees that have given authority
     */
    List<EmployeeNames> findAllByAuthoritiesContains(UserAuthorities authorities);

    /**
     * @param authorities {@link UserAuthorities authority}
     * @return All employees that have given authority and not archived
     */
    int countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities authorities);

    /**
     * @param username Username
     * @return {@link Optional} with {@link Employee employee} with provided username or empty Optional if none found.
     */
    Optional<Employee> findByUsername(String username);

    /**
     * @param username Username
     * @return {@code true} if there is user with provided name
     */
    boolean existsByUsername(String username);

    /**
     * Changes password of the user with given email.
     *
     * @param username    Username identifying the user
     * @param newPassword New password
     */
    @Modifying
    @Query("update Employee e set e.password = ?2 where e.username = ?1")
    void changePassword(String username, String newPassword);

    /**
     * Deletes employees with the given IDs.
     *
     * @param ids The collection of employee IDs.
     */
    @Modifying
    @Query("delete from Employee e where e.id in ?1")
    void deleteByIdIn(Collection<Long> ids);

    /**
     * Counts employees that are not archived and
     * has {@link dk.ledocsystem.ledoc.model.Customer} with provided ID.
     */
    long countByCustomerIdAndArchivedFalse(Long customerId);

    /**
     * @param authorities List of authorities
     * @return All {@link Employee} employees that contains given authorities
     */
    List<EmployeeDataExcel> findAllByAuthoritiesIn(List<UserAuthorities> authorities);

    @Override
    default void customize(QuerydslBindings bindings, QEmployee root) {
        bindings.including(QEmployee.employee.archived, QEmployee.employee.responsible.id,
                QEmployee.employee.authorities, QEmployee.employee.username, QEmployee.employee.firstName,
                QEmployee.employee.lastName, QEmployee.employee.cellPhone, QEmployee.employee.idNumber,
                QEmployee.employee.initials, QEmployee.employee.phoneNumber, QEmployee.employee.details.title,
                QEmployee.employee.nearestRelative.email, QEmployee.employee.nearestRelative.phoneNumber,
                QEmployee.employee.personalInfo.personalMobile, QEmployee.employee.personalInfo.privateEmail,
                ExpressionUtils.path(Employee.class, QEmployee.employee, "locations.id"));
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
