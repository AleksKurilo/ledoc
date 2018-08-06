package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.trade.UniqueTradeName;
import lombok.Data;

@Data
public class TradeDTO {

    @UniqueTradeName
    private String name;
}
