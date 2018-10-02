package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.trades.TradeCreateDTO;
import dk.ledocsystem.ledoc.model.Trade;

import java.util.List;

public interface TradeService {

    List<Trade> getAll();

    Trade createNew(TradeCreateDTO tradeCreateDTO);

    void deleteById(Long tradeId);
}
