package cache;

import bot.BotState;

import java.util.HashMap;
import java.util.Map;

/**
 * Хранит информацию о том, какое состояние бота соответствует каждому пользователю.
 */
public class UserDataCache implements DataCache {
    private Map<Integer, BotState> userBotStates = new HashMap<>();
    private Map<Integer, UserProfileData> usersProfileData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        userBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = userBotStates.get(userId);
        if (botState == null){
            botState = BotState.START_PAGE;
        }
        return botState;
    }

    @Override
    public UserProfileData getUserProfileData(int userId) {
        UserProfileData data = usersProfileData.get(userId);
        if (data==null) data = new UserProfileData();
        return data;
    }

    @Override
    public void setUserProfileData(int userId, UserProfileData data) {
        usersProfileData.put(userId, data);
    }


}
