package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<Trade> findByName(String name);

    Set<Trade> findAllByIdIn(Set<Long> ids);
}
