package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    boolean existsByName(String name);
}
