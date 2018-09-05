package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.dto.projections.IdAndLocalizedName;
import dk.ledocsystem.ledoc.model.AuthenticationType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthenticationTypeRepository extends CrudRepository<AuthenticationType, Long> {

    boolean existsByNameEn(String nameEn);

    List<IdAndLocalizedName> getAllBy();

}
