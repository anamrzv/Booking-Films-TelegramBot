package handlers.films;

import cache.DataCache;
import handlers.BotState;
import handlers.InputMessageHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Этот класс отвечает за показ списка фильмов.
 */
public class FilmsShowHandler implements InputMessageHandler {
    private DataCache userDataCache;

    public FilmsShowHandler(DataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_FILMS;
    }

    private SendMessage processUserInput(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(message.getText());
        sendMessage.setReplyMarkup(getInlineMessageButtons());
        int userId = message.getFrom().getId();
        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_FILMS);
        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        InlineKeyboardButton sessionsButton = new InlineKeyboardButton();
        sessionsButton.setText("Сеансы");
        sessionsButton.setCallbackData("sessions");
        keyboardButtonsRow1.add(sessionsButton);

        InlineKeyboardButton videoButton = new InlineKeyboardButton();
        videoButton.setText("Трейлер");
        videoButton.setCallbackData("trailer");
        keyboardButtonsRow2.add(videoButton);

        InlineKeyboardButton descriptionButton = new InlineKeyboardButton();
        descriptionButton.setText("Описание фильма");
        descriptionButton.setCallbackData("description");
        keyboardButtonsRow2.add(descriptionButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }



}