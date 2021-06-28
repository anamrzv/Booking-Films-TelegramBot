package handlers;

import bot.BotState;
import cache.DataCache;
import cache.Film;
import cache.UserDataCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CallBackQueryFacade {
    private DataCache userDataCache;

    public CallBackQueryFacade(DataCache userDataCache){
        this.userDataCache = userDataCache;
    }

    public SendMessage processCallbackQuery(CallbackQuery buttonQuery) {
        final int userId = buttonQuery.getFrom().getId();
        SendMessage callBackAnswer = null;

        List<Film> films = DataBaseManager.getInstance().getFilmsFromDB();
        if (films == null) films = new LinkedList<>();

        String[] parts = buttonQuery.getData().split("\\|");
        String option = parts[0];
        String filmName = parts[1];
        if (option.equals("sessions")) {
            callBackAnswer = sendAnswerCallbackQuery("Сеансы на фильм " + filmName + ":\n", buttonQuery);
            callBackAnswer = processSessionsForFilm(callBackAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_SESSIONS);
        } else if (option.equals("description")) {
            String description = "";
            Iterator<Film> filmIterator = films.listIterator();
            while (filmIterator.hasNext()) {
                Film newFilm = filmIterator.next();
                if (newFilm.getTitle().equals(filmName)) {
                    description = newFilm.getDescription();
                    break;
                }
            }
            callBackAnswer = sendAnswerCallbackQuery("Описание фильма " + filmName + ":\n" + description, buttonQuery);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_DESCRIPTION);
        } else if (option.equals("trailer")) {
            String trailerURL = "";
            Iterator<Film> filmIterator = films.listIterator();
            while (filmIterator.hasNext()) {
                Film newFilm = filmIterator.next();
                if (newFilm.getTitle().equals(filmName)) {
                    trailerURL = newFilm.getTrailer();
                    break;
                }
            }
            callBackAnswer = sendAnswerCallbackQuery("Ссылка на трейлер фильма " + filmName + ":\n" + trailerURL, buttonQuery);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_VIDEO);
        } else userDataCache.setUsersCurrentBotState(userId, BotState.START_PAGE);
        return callBackAnswer;
    }

    /**
     * Заглушка для обработки сеансов
     * @param callBackAnswer
     * @return
     */
    private SendMessage processSessionsForFilm(SendMessage callBackAnswer){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton Button1 = new InlineKeyboardButton();
        Button1.setText("11:00");
        Button1.setCallbackData("11:00");
        keyboardButtonsRow1.add(Button1);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        callBackAnswer.setReplyMarkup(inlineKeyboardMarkup);
        return callBackAnswer;
    }

    private SendMessage sendAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        SendMessage answerCallbackQuery = new SendMessage();
        answerCallbackQuery.setChatId(callbackquery.getMessage().getChatId().toString());
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
