package handlers;

import bot.BotState;
import cache.DataCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import properties.Film;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Этот класс отвечает за показ списка фильмов.
 */
public class FilmsShowHandler implements InputMessageHandler {
    private DataCache userDataCache;
    private DataBaseManager manager;
    private List<String> titlesOfFilms = new LinkedList<>();
    private List<Film> films;
    private boolean iterationIsGoingOn;

    public FilmsShowHandler(DataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handleAsMessage(Message message) {
        return null;
    }

    @Override
    public SendPhoto handleAsPhoto(Message message) {return processUserInput(message);}

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_FILMS;
    }

    private SendPhoto processUserInput(Message message) {
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
            SendPhoto answer = new SendPhoto();
            answer.setChatId(message.getChatId().toString());
            answer.setPhoto(new InputFile("https://e7.pngegg.com/pngimages/251/931/png-clipart-iphone-emoji-sadness-smiley-emoji-electronics-face.png"));
            answer.setCaption("Фильмов пока нет");
            return answer;
        } else {
            String film = titlesOfFilms.get(0);
            SendPhoto answer = sendFilmBlocks(film, message);
            titlesOfFilms.remove(film);
            if (titlesOfFilms.isEmpty()) iterationIsGoingOn = false;
            return answer;
        }
    }

    private SendPhoto sendFilmBlocks(String filmName, Message message) {
        SendPhoto filmBlock = new SendPhoto();
        filmBlock.setChatId(message.getChatId().toString());
        filmBlock.setPhoto(new InputFile("https://image.freepik.com/free-photo/top-view-movie-lettering-on-yellow-background-with-copy-space_23-2148425108.jpg")); //Добавить загрузку фоток с бд/сервера
        filmBlock.setCaption(filmName);
        filmBlock.setReplyMarkup(getInlineMessageButtons(filmName));
        return filmBlock;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(String filmName) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        InlineKeyboardButton sessionsButton = new InlineKeyboardButton();
        sessionsButton.setText("Выбрать сеанс "+Emoji.TICKET.get());
        sessionsButton.setCallbackData("sessions|" + filmName);
        keyboardButtonsRow1.add(sessionsButton);

        InlineKeyboardButton videoButton = new InlineKeyboardButton();
        videoButton.setText("Трейлер "+Emoji.CLAPPER.get());
        videoButton.setCallbackData("trailer|" + filmName);
        keyboardButtonsRow2.add(videoButton);

        InlineKeyboardButton descriptionButton = new InlineKeyboardButton();
        descriptionButton.setText("Описание фильма "+Emoji.POPCORN.get());
        descriptionButton.setCallbackData("description|" + filmName);
        keyboardButtonsRow2.add(descriptionButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


}