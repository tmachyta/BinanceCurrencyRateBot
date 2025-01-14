package mtm.telegrambinanceapibot.service.callback.pay;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface PayCallbackQueryService {
    EditMessageMedia handleCallbackQuery(Update update);
}
