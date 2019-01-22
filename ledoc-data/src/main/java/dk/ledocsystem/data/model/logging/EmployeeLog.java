package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name="employee_logs")
public class EmployeeLog extends AbstractLog {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_employee_id", nullable = false)
    private Employee targetEmployee;

    @OneToMany(mappedBy = "log", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EmployeeEditDetails> editDetails;

    public void setEditDetails(List<EmployeeEditDetails> editDetails) {
        this.editDetails = editDetails;
        editDetails.forEach(details -> details.setLog(this));
    }
}
