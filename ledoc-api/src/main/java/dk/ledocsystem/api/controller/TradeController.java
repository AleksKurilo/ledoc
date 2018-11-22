package dk.ledocsystem.api.controller;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.service.api.TradeService;
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
    public List<Trade> getAllTrades() {
        return tradeService.getAll();
    }

    @RolesAllowed("super_admin")
    @PostMapping
    public Trade createNew(@RequestBody @Valid TradeCreateDTO createDTO) {
        return tradeService.createNew(createDTO);
    }

    @DeleteMapping("/{tradeId}")
    public void deleteById(@PathVariable Long tradeId) {
        tradeService.deleteById(tradeId);
    }
}
