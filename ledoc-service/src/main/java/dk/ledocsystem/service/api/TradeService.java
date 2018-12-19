package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.service.api.dto.outbound.GetTradeDTO;

import java.util.List;

public interface TradeService {

    List<GetTradeDTO> getAll();

    GetTradeDTO createNew(TradeCreateDTO tradeCreateDTO);

    void deleteById(Long tradeId);
}
