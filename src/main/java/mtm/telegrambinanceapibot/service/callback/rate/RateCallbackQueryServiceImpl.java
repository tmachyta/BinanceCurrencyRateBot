package mtm.telegrambinanceapibot.service.callback.rate;

import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.service.currency.CurrencyRateService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class RateCallbackQueryServiceImpl implements RateCallbackQueryService {
    private final CurrencyRateService currencyRateService;

    @Override
    public EditMessageText handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        String response = currencyRateService.handleCurrencyRate(callbackData);

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId((int) messageId);
        message.setText(response);

        return message;
    }
}
