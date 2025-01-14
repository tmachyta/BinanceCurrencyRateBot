package mtm.telegrambinanceapibot.service.help;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface HelpCommandService {
    SendMessage helpCommandReceived(Long chatId);
}
