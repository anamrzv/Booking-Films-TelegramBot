package handlers.films;

import cache.DataCache;
import handlers.BotState;
import handlers.InputMessageHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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
        int userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();


        SendMessage replyToUser = new SendMessage();
        replyToUser.setText("Фильм Однажды в голливуде");
        replyToUser.setChatId(chatId);
        replyToUser.setReplyMarkup(getInlineMessageButtons());

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_FILMS);

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton descriptionButton = new InlineKeyboardButton();
        descriptionButton.setText("Описание фильма");
        descriptionButton.setCallbackData("Фильм 'Однажды в Голливуде' ......");

        InlineKeyboardButton videoButton = new InlineKeyboardButton();
        videoButton.setText("Трейлер");
        videoButton.setCallbackData("Трейлер к фильму 'Однажды в Голливуде' ......");

        InlineKeyboardButton sessionsButton = new InlineKeyboardButton();
        sessionsButton.setText("Сеансы");
        sessionsButton.setCallbackData("Сеансы к фильму 'Однажды в Голливуде' ......");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(descriptionButton);
        keyboardButtonsRow1.add(videoButton);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(sessionsButton);

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
