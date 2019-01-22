package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.supplier.Supplier;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "supplier_logs")
public class SupplierLog extends AbstractLog {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "log", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SupplierEditDetails> editDetails;

    public void setEditDetails(List<SupplierEditDetails> editDetails) {
        this.editDetails = editDetails;
        editDetails.forEach(details -> details.setLog(this));
    }
}
