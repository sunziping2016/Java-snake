package common.model;

import java.io.Serializable;

/**
 * Created by sun on 4/28/16.
 *
 * Class represents command.
 */

/* Stateful network protocol for game login and play.
 *
 * Two state: `logged in` and `not logged in`
 * Following command marked with "+" means login required, while those marked with "-" means no special state needed.
 *
 * All these command may return RuntimeException when it failed.
 */
public class GameCommand implements Serializable {
    public enum Command {
        USER_REGISTER,      // - Submit a username and password and return null.
        USER_LOGIN,         // - Submit a username and password and return a UUID.
        USER_INFO,          // - Submit a UUID of user and return a UserInfo.
        USER_LOGOUT,        // + Submit a UUID of user and return null,
        GET_LOGIN_ID,       // - Return UUID of current user (null if not login).

        GAME_CREATE,        // + Return a UUID of game.
        GAME_DELETE,        // + Return null.
        GAME_LIST,          // - Return an ArrayList<UUID> of games.
        GAME_INFO,          // - Submit a UUID of game and return a GameInfo.
        GAME_JOIN,          // + Submit a UUID of game and return a null and then pass the input stream to the GamePlayClient..
    }
    public Command command;

    public GameCommand(Command command) {
        this.command = command;
    }
}
