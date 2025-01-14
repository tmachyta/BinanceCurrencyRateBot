package mtm.telegrambinanceapibot.service.callback.pay;

import lombok.RequiredArgsConstructor;
import mtm.telegrambinanceapibot.service.payment.PaymentCommandService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

@Service
@RequiredArgsConstructor
public class PayCallbackQueryServiceImpl implements PayCallbackQueryService {
    private final PaymentCommandService paymentCommandService;

    @Override
    public EditMessageMedia handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendPhoto sendPhoto = paymentCommandService.handlePaymentCommand(chatId, callbackData);

        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(sendPhoto.getPhoto().getAttachName());

        EditMessageMedia messageMedia = new EditMessageMedia();
        messageMedia.setChatId(chatId);
        messageMedia.setMessageId((int) messageId);
        messageMedia.setMedia(inputMediaPhoto);
        return messageMedia;
    }
}
