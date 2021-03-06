package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    boolean existsByNameEn(String name);

    boolean existsByNameDa(String name);
}
