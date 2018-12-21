package dk.ledocsystem.service.impl;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.repository.TradeRepository;
import dk.ledocsystem.service.api.TradeService;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
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
    public List<IdAndLocalizedName> getAll() {
        return tradeRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public IdAndLocalizedName createTrade(TradeCreateDTO tradeCreateDTO) {
        tradeCreateDtoValidator.validate(tradeCreateDTO);

        Trade trade = modelMapper.map(tradeCreateDTO, Trade.class);
        return mapToDto(tradeRepository.save(trade));
    }

    private IdAndLocalizedName mapToDto(Trade trade) {
        return modelMapper.map(trade, IdAndLocalizedName.class);
    }

    @Override
    public void deleteById(Long tradeId) {
        tradeRepository.deleteById(tradeId);
    }
}
