package mtm.telegrambinanceapibot.service.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {
    private static final Map<String, String> PAYMENT_PAIRS = Map.ofEntries(
            Map.entry("1 USDT", "https://imgur.com/a/26nhu00"),
            Map.entry("5 USDT", "Test2"),
            Map.entry("10 USDT", "Test3"),
            Map.entry("20 USDT", "Test4"),
            Map.entry("50 USDT", "Test5"),
            Map.entry("100 USDT", "Test6"),
            Map.entry("Your Amount", "Test7")

    );
    private static final String DEFAULT_HEADER = """
                Підтримайте розробників 🥹
                
                Ваші чайові — наша мотивація! На каву, канапку 😊 чи, можливо на BTC? 😄
                
                Оберіть Ваші Чайові :)
                
                """;

    @Override
    public SendMessage sendPaymentSelectionKeyboard(Long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        int count = 0;
        for (Map.Entry<String, String> entry : PAYMENT_PAIRS.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getValue());

            row.add(button);
            count++;

            if (count % 2 == 0) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
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
    public SendPhoto handlePaymentCommand(Long chatId, String photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(photo));
        return sendPhoto;
    }

    public SendMessage sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
}
