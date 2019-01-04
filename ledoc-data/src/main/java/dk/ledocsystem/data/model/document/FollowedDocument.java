package dk.ledocsystem.data.model.document;

import dk.ledocsystem.data.model.employee.Employee;
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
@Table(name = "followed_document")
@IdClass(FollowedDocumentId.class)
public class FollowedDocument implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Don not rename field. It's used for sorting on frontend part
    @Id
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document followed;

    @Column(name = "forced")
    private boolean forced;

    @Column(name = "read", nullable = false, columnDefinition = "false")
    private boolean read;
}
