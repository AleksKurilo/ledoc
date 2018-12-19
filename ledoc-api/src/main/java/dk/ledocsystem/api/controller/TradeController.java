package dk.ledocsystem.api.controller;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.service.api.TradeService;
import dk.ledocsystem.service.api.dto.outbound.GetTradeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public List<GetTradeDTO> getAllTrades() {
        return tradeService.getAll();
    }

    @RolesAllowed("super_admin")
    @PostMapping
    public GetTradeDTO createNew(@RequestBody @Valid TradeCreateDTO createDTO) {
        return tradeService.createNew(createDTO);
    }

    @DeleteMapping("/{tradeId}")
    public void deleteById(@PathVariable Long tradeId) {
        tradeService.deleteById(tradeId);
    }
}
