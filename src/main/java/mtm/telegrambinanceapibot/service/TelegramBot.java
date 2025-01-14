package mtm.telegrambinanceapibot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mtm.telegrambinanceapibot.config.BotConfig;
import mtm.telegrambinanceapibot.dto.CurrencyResponseDataDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String START_MESSAGE = """
                Вітаю, %s, у BinanceCurrenciesRates!
                
                Дізнайтесь більше інформації за допомогою команди /help
                
                """;
    private static final String HELP_MESSAGE = """
                
                Щоб запустити бота використовуйте команду /start
                
                Щоб дізнатись більше інформації використовуйте команду /help
                
                Щоб дізнатись курс пар використовуйте команду /rate
                
                Не знайшли відповідної пари? Використовуйте команду /rate <Symbol>
                
                Наприклад: /rate BTCUSDT
                
                """;
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String RATE = "/rate";
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
            Map.entry("LUNAUSDT", "LUNA/USDT"),
            Map.entry("CROUSDT", "CRO/USDT"),
            Map.entry("ALGOUSDT", "ALGO/USDT"),
            Map.entry("NEARUSDT", "NEAR/USDT"),
            Map.entry("XLMUSDT", "XLM/USDT"),
            Map.entry("ZRXUSDT", "ZRX/USDT"),
            Map.entry("FTTUSDT", "FTT/USDT"),
            Map.entry("IOTAUSDT", "IOTA/USDT"),
            Map.entry("STMXUSDT", "STMX/USDT"),
            Map.entry("SANDUSDT", "SAND/USDT"),
            Map.entry("LRCUSDT", "LRC/USDT"),
            Map.entry("MANAUSDT", "MANA/USDT"),
            Map.entry("RENUSDT", "REN/USDT"),
            Map.entry("TONSDT", "TON/USDT"),
            Map.entry("TWTUSDT", "TWT/USDT"),
            Map.entry("UAHUSDT", "UAH/USDT")
    );

    private final BotConfig botConfig;
    private final BinanceApiService binanceApiService;

    public TelegramBot(BotConfig botConfig, BinanceApiService binanceApiService) {
        this.botConfig = botConfig;
        this.binanceApiService = binanceApiService;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Запустити бота"));
        listOfCommands.add(new BotCommand("/help", "Отримати допомогу"));
        listOfCommands.add(new BotCommand("/rate", "Перевірити курс валют"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't find command");
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case START:
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case HELP:
                    helpCommandReceived(chatId);
                    break;

                case RATE:
                    sendCurrencySelectionKeyboard(chatId);
                    break;

                default:
                    if (messageText.startsWith("/rate")) {
                        handleCurrenciesRateCommand(messageText, chatId);
                    } else {
                        sendMessage(chatId, "Невідома команда. "
                                + "Спробуйте /help для списку доступних команд.");
                    }
                    break;
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (CURRENCY_PAIRS.containsKey(callbackData)) {
                EditMessageText message = new EditMessageText();
                message.setChatId(chatId);
                message.setMessageId((int) messageId);

                String rateMessage = handleCurrencyRate(callbackData);

                message.setText(rateMessage);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sendMessage(chatId, "Невідома валютна пара.");
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = String.format(START_MESSAGE, name);
        sendMessage(chatId, answer);
    }

    private void helpCommandReceived(Long chatId) {
        String answer = String.format(HELP_MESSAGE);
        sendMessage(chatId, answer);
    }

    private void currenciesRateByPair(Long chatId, String pair) throws IOException {
        CurrencyResponseDataDto currencyResponseDataDto =
                binanceApiService.getCurrency(pair);

        String formattedMessage = String.format(
                "Ціна для пари %s: %s USD",
                currencyResponseDataDto.getSymbol(),
                currencyResponseDataDto.getPrice()
        );

        sendMessage(chatId, formattedMessage);
    }

    private void handleCurrenciesRateCommand(String message, Long chatId) {
        if (message.matches(RATE + "\\s+\\w+")) {

            String pair = message.split("\\s+")[1];

            try {
                currenciesRateByPair(chatId, pair);
            } catch (IOException e) {
                sendMessage(chatId, "Помилка під час отримання даних для пари: " + pair);
            }
        } else {
            sendMessage(chatId, "Невірний формат команди. Використовуйте: /rate <SYMBOL>");
        }
    }

    private void sendCurrencySelectionKeyboard(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Оберіть валютну пару:");

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

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Помилка відправки клавіатури: " + e.getMessage());
        }
    }

    private String handleCurrencyRate(String pair) {
        if (CURRENCY_PAIRS.containsKey(pair)) {
            try {
                CurrencyResponseDataDto currencyResponseDataDto =
                        binanceApiService.getCurrency(pair);
                return String.format(
                        "Курс для пари %s (%s): %.4f",
                        pair,
                        CURRENCY_PAIRS.get(pair),
                        currencyResponseDataDto.getPrice()
                );
            } catch (IOException e) {
                return "Не вдалося отримати дані для пари: " + pair;
            }
        } else {
            return "Ця пара недоступна.";
        }
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegram failed: " + e.getMessage());
        }
    }
}
