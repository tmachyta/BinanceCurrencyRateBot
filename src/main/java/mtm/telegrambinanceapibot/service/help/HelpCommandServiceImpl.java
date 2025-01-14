package mtm.telegrambinanceapibot.service.help;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class HelpCommandServiceImpl implements HelpCommandService {
    private static final String HELP_MESSAGE = """
                
                Щоб запустити бота використовуйте команду /start
                
                Щоб дізнатись більше інформації використовуйте команду /help
                
                Щоб дізнатись курс пар використовуйте команду /rate
                
                Не знайшли відповідної пари? Використовуйте команду /rate <Symbol>
                
                Наприклад: /rate BTCUSDT
                
                Підтримати розробників 🥹 - /tips
                
                """;

    @Override
    public SendMessage helpCommandReceived(Long chatId) {
        String answer = String.format(HELP_MESSAGE);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        return sendMessage;
    }
}
