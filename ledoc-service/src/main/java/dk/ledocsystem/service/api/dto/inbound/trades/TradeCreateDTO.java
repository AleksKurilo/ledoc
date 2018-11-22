package dk.ledocsystem.service.api.dto.inbound.trades;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import dk.ledocsystem.service.api.validation.trade.UniqueTradeNameEn;
import dk.ledocsystem.service.api.validation.trade.UniqueTradeNameDa;
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
