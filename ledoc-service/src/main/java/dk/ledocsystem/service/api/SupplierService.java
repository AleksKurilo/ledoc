package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.supplier.SupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.supplier.GetSupplierDTO;
import dk.ledocsystem.service.api.dto.outbound.supplier.SupplierPreviewDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface SupplierService extends CustomerBasedDomainService<GetSupplierDTO> {

    GetSupplierDTO create(SupplierDTO supplierDTO, UserDetails currentUser);

    GetSupplierDTO update(SupplierDTO supplierDTO, UserDetails currentUser);

    Optional<SupplierPreviewDTO> getPreviewDtoById(Long id, boolean isSaveLog, UserDetails currentUser);

    void changeArchivedStatus(Long id, ArchivedStatusDTO archivedStatusDTO, UserDetails currentUser);
}
