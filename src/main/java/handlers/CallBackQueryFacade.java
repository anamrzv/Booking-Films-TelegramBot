package handlers;

import bot.BotState;
import cache.DataCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import properties.Film;
import properties.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CallBackQueryFacade {
    private DataCache userDataCache;

    public CallBackQueryFacade(DataCache userDataCache) {
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
            callBackAnswer = sendAnswerCallbackQuery("Выберите дату сеанса фильма " + filmName + ":\n", buttonQuery);
            callBackAnswer = processSessionsForFilm(callBackAnswer, filmName, films);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_SESSIONS);
        } else if (option.equals("time")) {
            callBackAnswer = sendAnswerCallbackQuery("Выберите время сеанса фильма " + "\n", buttonQuery);
            callBackAnswer = processTimesForFilm(callBackAnswer, filmName, films);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_TIME);
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
            callBackAnswer = sendAnswerCallbackQuery("Ссылка на трейлер фильма " + filmName + ":\n" + "https://www.youtube.com/" + trailerURL, buttonQuery);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_VIDEO);
        } else if (option.equals("link")) {
            String link = "\nhttp://45.84.225.161/film/" + filmName;
            callBackAnswer = sendAnswerCallbackQuery("Ссылка на бронирование мест: " + link, buttonQuery);
            userDataCache.setUsersCurrentBotState(userId, BotState.GIVE_LINK);
        } else userDataCache.setUsersCurrentBotState(userId, BotState.START_PAGE);


        return callBackAnswer;
    }

    /**
     * Заглушка для обработки сеансов
     *
     * @param callBackAnswer
     * @return
     */
    private SendMessage processSessionsForFilm(SendMessage callBackAnswer, String filmName, List<Film> films) {
        int filmId = -1;
        List<Session> listOfSessions = DataBaseManager.getInstance().getSessionsFromDB();
        List<Session> listOfSessionsForFilmById;

        Iterator<Film> filmIterator = films.listIterator();
        while (filmIterator.hasNext()) {
            Film newFilm = filmIterator.next();
            if (newFilm.getTitle().equals(filmName)) {
                filmId = newFilm.getId();
                break;
            }
        }

        int finalFilmId = filmId;
        listOfSessionsForFilmById = listOfSessions.stream()
                .filter(session -> session.getFilmId() == finalFilmId)
                .collect(Collectors.toList());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        if (listOfSessionsForFilmById.size() == 0) callBackAnswer.setText("Сеансов на фильм " + filmName + " нет:(");
        else {
            for (int i = 0; i < listOfSessionsForFilmById.size(); i++) {
                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                InlineKeyboardButton Button1 = new InlineKeyboardButton();
                Button1.setText(listOfSessionsForFilmById.get(i).getDay());
                Button1.setCallbackData("time|" + listOfSessionsForFilmById.get(i).getDay() + " " + listOfSessionsForFilmById.get(i).getFilmId());
                keyboardButtonsRow1.add(Button1);
                rowList.add(keyboardButtonsRow1);
            }
            inlineKeyboardMarkup.setKeyboard(rowList);
            callBackAnswer.setReplyMarkup(inlineKeyboardMarkup);
        }
        return callBackAnswer;
    }

    private SendMessage processTimesForFilm(SendMessage callBackAnswer, String dateAndFilm, List<Film> films) {
        String date = dateAndFilm.substring(0, dateAndFilm.indexOf(" "));
        int filmID = Integer.parseInt(dateAndFilm.substring(dateAndFilm.indexOf(" ") + 1));

        List<Session> listOfSessions = DataBaseManager.getInstance().getSessionsFromDB();
        List<Session> listOfSessionsForFilmById = new ArrayList<>();

        for (int i = 0; i < listOfSessions.size(); i++) {
            if (date.equals(listOfSessions.get(i).getDay()) && listOfSessions.get(i).getFilmId() == filmID)
                listOfSessionsForFilmById.add(listOfSessions.get(i));
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 0; i < listOfSessionsForFilmById.size(); i++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText((listOfSessionsForFilmById.get(i).getTime()));
            button.setCallbackData("link|" + filmID);
            keyboardButtonsRow.add(button);
            rowList.add(keyboardButtonsRow);
        }
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
