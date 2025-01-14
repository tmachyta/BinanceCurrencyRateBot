package mtm.telegrambinanceapibot.service.currency;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CurrencyRateService {
    SendMessage sendCurrencySelectionKeyboard(Long chatId);

    String handleCurrencyRate(String pair);
}
