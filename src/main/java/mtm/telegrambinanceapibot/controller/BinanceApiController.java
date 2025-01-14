package mtm.telegrambinanceapibot.controller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.dto.CurrencyResponseDataDto;
import mtm.telegrambinanceapibot.service.binance.BinanceApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currencies")
public class BinanceApiController {
    private final BinanceApiService binanceApiService;

    @GetMapping("/spotRate")
    public CurrencyResponseDataDto getCurrency(@RequestParam String pair) throws IOException {
        return binanceApiService.getCurrency(pair);
    }

    @GetMapping("/futuresRate")
    public CurrencyResponseDataDto getFuturesCurrency(@RequestParam String pair)
            throws IOException {
        return binanceApiService.getFuturesCurrency(pair);
    }
}
