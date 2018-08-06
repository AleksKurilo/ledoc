package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.model.Trade;
import dk.ledocsystem.ledoc.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class TradeController {

    private final TradeRepository tradeRepository;

    @GetMapping()
    public List<Trade> getAlltrades() {
        return tradeRepository.findAll();
    }
}
