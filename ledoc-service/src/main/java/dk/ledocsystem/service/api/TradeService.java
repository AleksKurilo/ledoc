package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;

import java.util.List;

public interface TradeService {

    List<IdAndLocalizedName> getAll();

    IdAndLocalizedName createTrade(TradeCreateDTO tradeCreateDTO);

    void deleteById(Long tradeId);
}
