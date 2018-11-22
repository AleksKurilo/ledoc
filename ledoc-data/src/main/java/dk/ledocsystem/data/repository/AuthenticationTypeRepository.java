package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.projections.IdAndLocalizedName;
import dk.ledocsystem.data.model.equipment.AuthenticationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthenticationTypeRepository extends CrudRepository<AuthenticationType, Long> {

    boolean existsByNameEn(String nameEn);

    List<IdAndLocalizedName> getAllBy();

    Page<IdAndLocalizedName> getAllBy(Pageable pageable);

}
