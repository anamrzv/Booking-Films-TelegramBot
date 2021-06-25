package handlers.films;

import cache.DataCache;
import handlers.BotState;
import handlers.InputMessageHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

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

    private SendMessage processUserInput(Message message){
        int userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();

        SendMessage replyToUser = new SendMessage(chatId, "Список фильмов");
        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_FILMS);

        return replyToUser;
    }
}
