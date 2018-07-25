package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
