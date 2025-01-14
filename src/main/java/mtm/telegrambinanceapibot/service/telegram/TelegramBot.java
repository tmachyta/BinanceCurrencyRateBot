package mtm.telegrambinanceapibot.service.telegram;

import java.util.ArrayList;
import java.util.List;
import mtm.telegrambinanceapibot.config.BotConfig;
import mtm.telegrambinanceapibot.service.callback.pay.PayCallbackQueryService;
import mtm.telegrambinanceapibot.service.callback.rate.RateCallbackQueryService;
import mtm.telegrambinanceapibot.service.currency.CurrencyRateService;
import mtm.telegrambinanceapibot.service.help.HelpCommandService;
import mtm.telegrambinanceapibot.service.payment.PaymentCommandService;
import mtm.telegrambinanceapibot.service.rate.RateCommandService;
import mtm.telegrambinanceapibot.service.start.StartCommandService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String RATE = "/rate";
    private static final String TIPS = "/tips";
    private static final String ERROR_ONE = "Невідома команда. "
            + "Спробуйте /help для списку доступних команд.";
    private static final String ERROR_TWO = "Сталася помилка під час "
            + "виконання вашої команди. Спробуйте ще раз.";
    private static final String START_INFO = "Запустити бота";
    private static final String HELP_INFO = "Отримати допомогу";
    private static final String RATE_INFO = "Перевірити курс валют";
    private static final String TIPS_INFO = "Підтримати розробників 🥹";
    private final BotConfig botConfig;
    private final StartCommandService startCommandService;
    private final HelpCommandService helpCommandService;
    private final RateCommandService rateCommandService;
    private final CurrencyRateService currencyRateService;
    private final PaymentCommandService paymentCommandService;
    private final RateCallbackQueryService rateCallbackQueryService;
    private final PayCallbackQueryService payCallbackQueryService;

    public TelegramBot(BotConfig botConfig,
                       StartCommandService startCommandService,
                       HelpCommandService helpCommandService,
                       RateCommandService rateCommandService,
                       CurrencyRateService currencyRateService,
                       PaymentCommandService paymentCommandService,
                       RateCallbackQueryService rateCallbackQueryService,
                       PayCallbackQueryService payCallbackQueryService) {
        this.botConfig = botConfig;
        this.startCommandService = startCommandService;
        this.helpCommandService = helpCommandService;
        this.rateCommandService = rateCommandService;
        this.currencyRateService = currencyRateService;
        this.paymentCommandService = paymentCommandService;
        this.rateCallbackQueryService = rateCallbackQueryService;
        this.payCallbackQueryService = payCallbackQueryService;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START, START_INFO));
        listOfCommands.add(new BotCommand(HELP, HELP_INFO));
        listOfCommands.add(new BotCommand(RATE, RATE_INFO));
        listOfCommands.add(new BotCommand(TIPS, TIPS_INFO));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't find command " + e.getMessage());
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

            try {
                switch (messageText) {
                    case START:
                        execute(startCommandService.startCommandReceived(chatId,
                                update.getMessage().getChat().getFirstName()));
                        break;

                    case HELP:
                        execute(helpCommandService.helpCommandReceived(chatId));
                        break;

                    case RATE:
                        execute(currencyRateService.sendCurrencySelectionKeyboard(chatId));
                        break;

                    case TIPS:
                        execute(paymentCommandService.sendPaymentSelectionKeyboard(chatId));
                        break;

                    default:
                        if (messageText.startsWith(RATE)) {
                            execute(rateCommandService.handleCurrenciesRateCommand(
                                    messageText, chatId));
                        } else {
                            sendMessage(chatId, ERROR_ONE);
                        }
                        break;
                }
            } catch (TelegramApiException e) {
                sendMessage(chatId, ERROR_TWO);
            }
        }

        if (update.hasCallbackQuery()) {
            try {
                String callback = update.getCallbackQuery().getData();
                if (callback.contains("USDT")) {
                    execute(rateCallbackQueryService.handleCallbackQuery(update));
                } else if (callback.contains("https://")) {
                    execute(payCallbackQueryService.handleCallbackQuery(update));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
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
