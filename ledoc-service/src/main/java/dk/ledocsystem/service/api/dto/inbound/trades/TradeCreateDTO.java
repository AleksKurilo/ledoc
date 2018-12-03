package dk.ledocsystem.service.api.dto.inbound.trades;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.trade.UniqueTradeNameEn;
import dk.ledocsystem.service.api.validation.trade.UniqueTradeNameDa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeCreateDTO {

    @NonCyrillic
    @UniqueTradeNameEn
    private String nameEn;

    @UniqueTradeNameDa
    private String nameDa;
}
