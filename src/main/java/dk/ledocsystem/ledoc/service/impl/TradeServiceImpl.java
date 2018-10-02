package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.trades.TradeCreateDTO;
import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import dk.ledocsystem.ledoc.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;

    @Override
    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }

    @Override
    public Trade createNew(TradeCreateDTO tradeCreateDTO) {
        Trade trade = new Trade();
        trade.setNameEn(tradeCreateDTO.getNameEn());
        trade.setNameDa(tradeCreateDTO.getNameDa());
        return tradeRepository.save(trade);
    }

    @Override
    public void deleteById(Long tradeId) {
        tradeRepository.deleteById(tradeId);
    }
}
