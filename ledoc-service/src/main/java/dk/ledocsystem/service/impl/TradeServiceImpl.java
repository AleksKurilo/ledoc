package dk.ledocsystem.service.impl;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.repository.TradeRepository;
import dk.ledocsystem.service.api.TradeService;
import dk.ledocsystem.service.api.dto.outbound.GetTradeDTO;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final BaseValidator<TradeCreateDTO> tradeCreateDtoValidator;
    private final ModelMapper modelMapper;

    @Override
    public List<GetTradeDTO> getAll() {
        return tradeRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public GetTradeDTO createNew(TradeCreateDTO tradeCreateDTO) {
        tradeCreateDtoValidator.validate(tradeCreateDTO);

        Trade trade = modelMapper.map(tradeCreateDTO, Trade.class);
        return mapToDto(tradeRepository.save(trade));
    }

    private GetTradeDTO mapToDto(Trade trade) {
        return modelMapper.map(trade, GetTradeDTO.class);
    }

    @Override
    public void deleteById(Long tradeId) {
        tradeRepository.deleteById(tradeId);
    }
}
