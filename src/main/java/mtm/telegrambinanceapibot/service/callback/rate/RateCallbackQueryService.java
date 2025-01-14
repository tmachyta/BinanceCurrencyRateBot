package mtm.telegrambinanceapibot.service.callback.rate;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface RateCallbackQueryService {
    EditMessageText handleCallbackQuery(Update update);
}
