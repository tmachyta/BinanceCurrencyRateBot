package mtm.telegrambinanceapibot.dto;

import lombok.Data;

@Data
public class CurrencyResponseDataDto {
    private String symbol;
    private double price;
}
