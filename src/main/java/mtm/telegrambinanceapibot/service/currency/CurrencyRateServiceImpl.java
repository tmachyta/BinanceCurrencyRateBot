package mtm.telegrambinanceapibot.service.currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.dto.CurrencyResponseDataDto;
import mtm.telegrambinanceapibot.service.binance.BinanceApiService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Service
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private static final Map<String, String> CURRENCY_PAIRS = Map.ofEntries(
            Map.entry("BTCUSDT", "BTC/USDT"),
            Map.entry("ETHUSDT", "ETH/USDT"),
            Map.entry("BNBUSDT", "BNB/USDT"),
            Map.entry("XRPUSDT", "XRP/USDT"),
            Map.entry("ADAUSDT", "ADA/USDT"),
            Map.entry("SOLUSDT", "SOL/USDT"),
            Map.entry("DOTUSDT", "DOT/USDT"),
            Map.entry("DOGEUSDT", "DOGE/USDT"),
            Map.entry("LTCUSDT", "LTC/USDT"),
            Map.entry("BCHUSDT", "BCH/USDT"),
            Map.entry("MATICUSDT", "MATIC/USDT"),
            Map.entry("TRXUSDT", "TRX/USDT"),
            Map.entry("AVAXUSDT", "AVAX/USDT"),
            Map.entry("FTMUSDT", "FTM/USDT"),
            Map.entry("LINKUSDT", "LINK/USDT"),
            Map.entry("SHIBUSDT", "SHIB/USDT"),
            Map.entry("VETUSDT", "VET/USDT"),
            Map.entry("EOSUSDT", "EOS/USDT"),
            Map.entry("CROUSDT", "CRO/USDT"),
            Map.entry("ALGOUSDT", "ALGO/USDT"),
            Map.entry("NEARUSDT", "NEAR/USDT"),
            Map.entry("XLMUSDT", "XLM/USDT"),
            Map.entry("ZRXUSDT", "ZRX/USDT"),
            Map.entry("FTTUSDT", "FTT/USDT"),
            Map.entry("SANDUSDT", "SAND/USDT"),
            Map.entry("MANAUSDT", "MANA/USDT"),
            Map.entry("RENUSDT", "REN/USDT"),
            Map.entry("TONSDT", "TON/USDT"),
            Map.entry("TWTUSDT", "TWT/USDT")
    );
    private static final String DEFAULT_HEADER = "Оберіть валютну пару:";
    private static final String HEADER_TWO = "Курс для пари %s (%s): %.4f";
    private static final String ERROR_ONE = "Не вдалося отримати дані для пари: ";
    private static final String ERROR_TWO = "Ця пара недоступна.";
    private final BinanceApiService binanceApiService;

    @Override
    public SendMessage sendCurrencySelectionKeyboard(Long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        int buttonCount = 0;
        for (Map.Entry<String, String> entry : CURRENCY_PAIRS.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getValue());
            button.setCallbackData(entry.getKey());

            row.add(button);
            buttonCount++;

            if (buttonCount == 3) {
                keyboard.add(row);
                row = new ArrayList<>();
                buttonCount = 0;
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(row);
        }
        SendMessage message = sendMessage(chatId, DEFAULT_HEADER);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    @Override
    public String handleCurrencyRate(String pair) {
        if (CURRENCY_PAIRS.containsKey(pair)) {
            try {
                CurrencyResponseDataDto currencyResponseDataDto =
                        binanceApiService.getCurrency(pair);
                return String.format(
                        HEADER_TWO,
                        pair,
                        CURRENCY_PAIRS.get(pair),
                        currencyResponseDataDto.getPrice()
                );
            } catch (IOException e) {
                return ERROR_ONE + pair;
            }
        } else {
            return ERROR_TWO;
        }
    }

    public SendMessage sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
}
