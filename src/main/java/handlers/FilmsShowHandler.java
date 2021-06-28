package handlers;

import bot.BotState;
import cache.DataCache;
import properties.Film;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Этот класс отвечает за показ списка фильмов.
 */
public class FilmsShowHandler implements InputMessageHandler {
    private DataCache userDataCache;
    private DataBaseManager manager;
    private List<String> titlesOfFilms= new LinkedList<>();
    private List<Film> films;
    private boolean iterationIsGoingOn;

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
        if (!iterationIsGoingOn) {
            int userId = message.getFrom().getId();
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_FILMS);
            manager = DataBaseManager.getInstance();
            films = manager.getListOfFilms();
            for (Film film : films) {
                titlesOfFilms.add(film.getTitle());
            }
            if (!films.isEmpty()) iterationIsGoingOn = true;
        }
        if (films.isEmpty() && iterationIsGoingOn == false) {
            SendMessage answer = new SendMessage(message.getChatId().toString(), "Пока фильмов нет:(");
            return answer;
        } else {
            String film = titlesOfFilms.get(0);
            SendMessage answer = sendFilmBlocks(film, message);
            titlesOfFilms.remove(film);
            if (titlesOfFilms.isEmpty()) iterationIsGoingOn = false;
            return answer;
        }
    }

    private SendMessage sendFilmBlocks(String filmName, Message message) {
        SendMessage filmBlock = new SendMessage();
        filmBlock.enableMarkdown(true);
        filmBlock.setChatId(message.getChatId().toString());
        filmBlock.setText(filmName);
        filmBlock.setReplyMarkup(getInlineMessageButtons(filmName));
        return filmBlock;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(String filmName) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        InlineKeyboardButton sessionsButton = new InlineKeyboardButton();
        sessionsButton.setText("Сеансы фильма");
        sessionsButton.setCallbackData("sessions|"+filmName);
        keyboardButtonsRow1.add(sessionsButton);

        InlineKeyboardButton videoButton = new InlineKeyboardButton();
        videoButton.setText("Трейлер");
        videoButton.setCallbackData("trailer|"+filmName);
        keyboardButtonsRow2.add(videoButton);

        InlineKeyboardButton descriptionButton = new InlineKeyboardButton();
        descriptionButton.setText("Описание фильма");
        descriptionButton.setCallbackData("description|"+filmName);
        keyboardButtonsRow2.add(descriptionButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


}