package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.equipment.AuthenticationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationTypeRepository extends JpaRepository<AuthenticationType, Long> {

    boolean existsByNameEn(String nameEn);

}
