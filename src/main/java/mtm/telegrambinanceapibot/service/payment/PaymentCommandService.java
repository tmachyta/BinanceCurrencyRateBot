package mtm.telegrambinanceapibot.service.payment;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public interface PaymentCommandService {
    SendMessage sendPaymentSelectionKeyboard(Long chatId);

    SendPhoto handlePaymentCommand(Long chatId, String photo);
}
