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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CallBackQueryFacade {
    private final DataCache userDataCache;

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
        String filmName = null;
        int filmId = Integer.parseInt(parts[1]);
        for (Film film : films) {
            if (film.getId() == filmId) {
                filmName = film.getTitle();
                break;
            }
        }
        switch (option) {
            case "sessions":
                callBackAnswer = sendAnswerCallbackQuery("Выберите дату сеанса фильма " + filmName + ":\n", buttonQuery);
                processSessionsForFilm(callBackAnswer, filmName, films);
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_SESSIONS);
                break;
            case "description": {
                String description = "";
                for (Film newFilm : films) {
                    if (newFilm.getTitle().equals(filmName)) {
                        description = newFilm.getDescription();
                        break;
                    }
                }
                callBackAnswer = sendAnswerCallbackQuery("Описание фильма " + filmName + ":\n" + description, buttonQuery);
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_DESCRIPTION);
                break;
            }
            case "trailer": {
                String trailerURL = "";
                for (Film newFilm : films) {
                    if (newFilm.getTitle().equals(filmName)) {
                        trailerURL = newFilm.getTrailer();
                        break;
                    }
                }
                callBackAnswer = sendAnswerCallbackQuery("Ссылка на трейлер фильма " + filmName + ":\n" + "https://www.youtube.com/" + trailerURL, buttonQuery);
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_VIDEO);
                break;
            }
            case "link":
                String link = "http://45.84.225.161/film/" + filmId;
                callBackAnswer = sendAnswerCallbackQuery("Ссылка на бронирование мест: " + link, buttonQuery);
                userDataCache.setUsersCurrentBotState(userId, BotState.GIVE_LINK);
                break;
            default:
                userDataCache.setUsersCurrentBotState(userId, BotState.START_PAGE);
                break;
        }


        return callBackAnswer;
    }

    private void processSessionsForFilm(SendMessage callBackAnswer, String filmName, List<Film> films) {
        int filmId = -1;
        List<Session> listOfSessions = DataBaseManager.getInstance().getSessionsFromDB();
        List<Session> listOfSessionsForFilmById;

        for (Film newFilm : films) {
            if (newFilm.getTitle().equals(filmName)) {
                filmId = newFilm.getId();
                break;
            }
        }

        int finalFilmId = filmId;
        listOfSessionsForFilmById = listOfSessions.stream()
                .filter(session -> session.getFilmId() == finalFilmId)
                .collect(Collectors.toList());
        System.out.println(listOfSessionsForFilmById);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        if (listOfSessionsForFilmById.size() == 0) callBackAnswer.setText("Сеансов на фильм " + filmName + " нет:(");
        else {
            for (Session session : listOfSessionsForFilmById) {
                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                InlineKeyboardButton Button1 = new InlineKeyboardButton();
                Button1.setText(session.getDay() + " " + session.getTime());
                Button1.setCallbackData("link|" + filmId);
                keyboardButtonsRow1.add(Button1);
                rowList.add(keyboardButtonsRow1);
            }
            inlineKeyboardMarkup.setKeyboard(rowList);
            callBackAnswer.setReplyMarkup(inlineKeyboardMarkup);
        }
    }

    private SendMessage sendAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        SendMessage answerCallbackQuery = new SendMessage();
        answerCallbackQuery.setChatId(callbackquery.getMessage().getChatId().toString());
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
