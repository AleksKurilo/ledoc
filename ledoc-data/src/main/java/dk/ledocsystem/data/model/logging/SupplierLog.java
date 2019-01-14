package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.supplier.Supplier;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "SupplierLog")
@Table(name = "supplier_logs")
public class SupplierLog extends AbstractLog {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
