package mtm.telegrambinanceapibot.service.rate;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.dto.CurrencyResponseDataDto;
import mtm.telegrambinanceapibot.service.binance.BinanceApiService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class RateCommandServiceImpl implements RateCommandService {
    private static final String REGEX = "\\s+\\w+";
    private static final String SECOND_REGEX = "\\s+";
    private static final String ERROR_MESSAGE_ONE = "Виникла помилка під час "
            + "отримання курсу валют. Спробуйте ще раз.";
    private static final String ERROR_MESSAGE_TWO = "Команда не розпізнана. "
            + "Використовуйте формат: /rate [валютна пара]";
    private static final String DEFAULT_HEADER = "Ціна для пари %s: %s USD";
    private static final int DEFAULT_NUMBER = 1;
    private static final String RATE = "/rate";
    private final BinanceApiService binanceApiService;

    @Override
    public SendMessage currenciesRateByPair(Long chatId, String pair) throws IOException {
        CurrencyResponseDataDto currencyResponseDataDto =
                binanceApiService.getCurrency(pair);

        String formattedMessage = String.format(
                DEFAULT_HEADER,
                currencyResponseDataDto.getSymbol(),
                currencyResponseDataDto.getPrice()
        );

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(formattedMessage);

        return sendMessage;
    }

    @Override
    public SendMessage handleCurrenciesRateCommand(String message, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (message.matches(RATE + REGEX)) {
            String pair = message.split(SECOND_REGEX)[DEFAULT_NUMBER];

            try {
                sendMessage = currenciesRateByPair(chatId, pair);
            } catch (IOException e) {
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(ERROR_MESSAGE_ONE);
            }
        } else {
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(ERROR_MESSAGE_TWO);
        }
        return sendMessage;
    }
}
