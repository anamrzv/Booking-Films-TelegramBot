package handlers;

import bot.BotState;
import cache.DataCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class ShowInfoHandler implements InputMessageHandler{
    private final DataCache userDataCache;

    public ShowInfoHandler(DataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendPhoto handleAsPhoto(Message message){ return null;}

    @Override
    public SendMessage handleAsMessage(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_INFO;
    }

    private SendMessage processUserInput(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("_Кинотеатр \"ob6\"_ " +
                "\nРады видеть Вас в нашем кинотеатре!" +
                "\nhttp://45.84.225.161/"+
                "\nНажмите на кнопку 'Фильмы', чтобы посмотреть, какие фильмы есть в прокате."+
                "\nКогда выберете сеанс, Вы получите ссылку на страницу с бронированием мест в кинотеатре." +
                "\nТакже Вы можете посмотреть интересующую информацию про фильм: его описание и трейлер."
        );
        sendMessage.setReplyMarkup(getReplyKeyboard());
        int userId = message.getFrom().getId();
        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_INFO);
        return sendMessage;
    }

    private ReplyKeyboardMarkup getReplyKeyboard() {
        return StartPageHandler.getReplyKeyboardMarkup();
    }
}

