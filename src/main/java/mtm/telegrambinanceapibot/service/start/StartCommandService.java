package mtm.telegrambinanceapibot.service.start;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface StartCommandService {
    SendMessage startCommandReceived(Long chatId, String name);
}
