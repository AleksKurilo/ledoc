package dk.ledocsystem.ledoc.dto.trades;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.trade.UniqueTradeNameEn;
import dk.ledocsystem.ledoc.annotations.validation.trade.UniqueTradeNameDa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeCreateDTO {

    @OnlyAscii
    @UniqueTradeNameEn
    private String nameEn;

    @UniqueTradeNameDa
    private String nameDa;
}
