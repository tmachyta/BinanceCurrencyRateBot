package mtm.telegrambinanceapibot.service.rate;

import java.io.IOException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface RateCommandService {
    SendMessage currenciesRateByPair(Long chatId, String pair) throws IOException;

    SendMessage handleCurrenciesRateCommand(String message, Long chatId);
}
