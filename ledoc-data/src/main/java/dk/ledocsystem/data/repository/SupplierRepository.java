package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    long countByCustomerId(Long customerId);

    long countByCustomerIdAndArchivedFalse(Long customerId);

}
