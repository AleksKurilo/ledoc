package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.data.model.Trade;

import java.util.List;

public interface TradeService {

    List<Trade> getAll();

    Trade createNew(TradeCreateDTO tradeCreateDTO);

    void deleteById(Long tradeId);
}
