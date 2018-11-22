package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity(name="EmployeeLog")
@Table(name="employee_logs")
public class EmployeeLog extends AbstractLog {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_employee_id")
    private Employee targetEmployee;
}
