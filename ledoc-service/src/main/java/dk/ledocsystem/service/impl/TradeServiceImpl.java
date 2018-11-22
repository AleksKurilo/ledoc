package dk.ledocsystem.service.impl;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.repository.TradeRepository;
import dk.ledocsystem.service.api.TradeService;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final BaseValidator<TradeCreateDTO> tradeCreateDtoValidator;

    @Override
    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }

    @Override
    public Trade createNew(TradeCreateDTO tradeCreateDTO) {
        tradeCreateDtoValidator.validate(tradeCreateDTO);

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
