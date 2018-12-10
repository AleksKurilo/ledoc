package dk.ledocsystem.data.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.*;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.data.projections.EmployeeSummary;
import dk.ledocsystem.data.util.LocalDateMultiValueBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
    void deleteByIdIn(Iterable<Long> ids);

    /**
     * @return All employees eligible for review
     */
    @Query("select e from Employee e join fetch e.details.responsibleOfSkills where e.archived = false")
    List<Employee> findAllForReview();

    @Query("select e.id as id, e.firstName as firstName, e.lastName as lastName, l.id as locations " +
            "from Employee e left join e.locations l where e.customer.id = ?1 and e.archived = false")
    List<EmployeeSummary> findAllBy(Long customerId);

    @Override
    default void customize(QuerydslBindings bindings, QEmployee root) {
        bindings.including(root.archived, root.responsible.id, root.creator.id, root.authorities, root.username,
                root.firstName, root.lastName, root.cellPhone, root.idNumber, root.initials, root.phoneNumber,
                root.title, root.nearestRelative.email, root.nearestRelative.phoneNumber, root.details.nextReviewDate,
                root.personalInfo.personalPhone, root.personalInfo.personalMobile, root.personalInfo.privateEmail,
                root.personalInfo.dateOfBirth, root.personalInfo.dayOfEmployment,
                ExpressionUtils.path(Employee.class, root, "locations.id"),
                ExpressionUtils.path(String.class, root, "name"),
                ExpressionUtils.path(String.class, root, "responsible.name"),
                ExpressionUtils.path(String.class, root, "nearestRelative.name"));
        bindings.bind(ExpressionUtils.path(String.class, root, "name"))
                .first((path, val) -> root.firstName.concat(" ").concat(root.lastName).containsIgnoreCase(val));
        bindings.bind(ExpressionUtils.path(String.class, root.responsible, "name"))
                .first((path, val) -> root.responsible.firstName.concat(" ").concat(root.responsible.lastName).containsIgnoreCase(val));
        bindings.bind(ExpressionUtils.path(String.class, root.nearestRelative, "name"))
                .first((path, val) -> root.nearestRelative.firstName.coalesce("").asString().concat(" ")
                        .concat(root.nearestRelative.lastName.coalesce("")).containsIgnoreCase(val));
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.bind(LocalDate.class).all(new LocalDateMultiValueBinding());
    }
}
