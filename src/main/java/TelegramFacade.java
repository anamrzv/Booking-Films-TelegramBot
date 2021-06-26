import cache.DataCache;
import cache.UserDataCache;
import handlers.BotState;
import handlers.BotStateContext;
import handlers.InputMessageHandler;
import handlers.films.FilmsShowHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TelegramFacade {
    private DataCache userDataCache = new UserDataCache();
    private final List<InputMessageHandler> messageHandlers;
    private BotStateContext botStateContext;
    private CallbackQueryFacade callbackQueryFacade = new CallbackQueryFacade();


    public TelegramFacade(){
        messageHandlers = new LinkedList<>();
        messageHandlers.add(new FilmsShowHandler(userDataCache));
        botStateContext = new BotStateContext(messageHandlers);
    }

    /**
     * Проверяем, не пустое ли сообщение/текстовое ли, передаем на обработку
     * @param update
     * @return
     */
    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()){
            log.info("New callbackQuery from User:{}, with data: {}",
                    update.getCallbackQuery().getFrom().getUserName(), update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatID:{}, with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    /**
     * Присваиваем боту для данного пользователя состояние, переходим в BotContext для подбора Handler'а
     * @param message
     * @return
     */
    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        int userID = message.getFrom().getId();
        SendMessage replyMessage;
        BotState botState;
        switch (inputMessage){
            case "Фильмы":
                botState = BotState.SHOW_FILMS;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userID);
        }
        userDataCache.setUsersCurrentBotState(userID, botState);
        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }
}
