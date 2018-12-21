package dk.ledocsystem.api.controller;

import dk.ledocsystem.service.api.dto.inbound.trades.TradeCreateDTO;
import dk.ledocsystem.service.api.TradeService;
import dk.ledocsystem.service.api.dto.outbound.IdAndLocalizedName;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public List<IdAndLocalizedName> getAllTrades() {
        return tradeService.getAll();
    }

    @RolesAllowed("super_admin")
    @PostMapping
    public IdAndLocalizedName createTrade(@RequestBody TradeCreateDTO createDTO) {
        return tradeService.createTrade(createDTO);
    }

    @DeleteMapping("/{tradeId}")
    public void deleteById(@PathVariable Long tradeId) {
        tradeService.deleteById(tradeId);
    }
}
