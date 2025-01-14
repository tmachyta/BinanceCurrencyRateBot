package mtm.telegrambinanceapibot.service.start;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class StartCommandServiceImpl implements StartCommandService {
    private static final String START_MESSAGE = """
                Вітаю, %s, у BinanceCurrenciesRates!
                
                Дізнайтесь більше інформації за допомогою команди /help
                
                """;

    @Override
    public SendMessage startCommandReceived(Long chatId, String name) {
        String answer = String.format(START_MESSAGE, name);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        return sendMessage;
    }
}
