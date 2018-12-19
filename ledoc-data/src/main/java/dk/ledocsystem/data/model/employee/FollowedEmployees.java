package dk.ledocsystem.data.model.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "followed_employees")
public class FollowedEmployees implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Don not rename field. It's used for sorting on frontend part
    @Id
    @ManyToOne
    @JoinColumn(name = "followed_employee_id")
    private Employee followed;

    @Column(name = "forced")
    private boolean forced;
}
