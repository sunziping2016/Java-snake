package common.model;

/**
 * Created by sun on 4/28/16.
 *
 * Class represents command.h
 */
public class GameCommand {
    enum Command {
        LOGIN,          // Submit a partial UserInfo and return a full UserInfo.
        CREATE,         // Return a RoomInfo with userInfo.
        LIST,           // Return an ArrayList<RoomInfo>.
        DELETE,         //
    }
    UserInfo userInfo;
}
