package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.trades.TradeCreateDTO;
import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.service.TradeService;
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

    @GetMapping("/all")
    public List<Trade> getAlltrades() {
        return tradeService.getAll();
    }

    @RolesAllowed("super_admin")
    @PostMapping("/new")
    public Trade createNew(@RequestBody @Valid TradeCreateDTO createDTO) {
        return tradeService.createNew(createDTO);
    }
}
