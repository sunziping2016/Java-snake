package server.model;

import common.controller.GameController;
import common.controller.GameFactory;
import common.model.GameInfo;
import common.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * GameServer side game management
 */
public class Games {

    private HashMap<UUID, GameController> games = new HashMap<>();
    private Users users;

    public Games(Users users) {
        this.users = users;
    }

    public synchronized UUID createGame(UUID userID) throws RuntimeException {
        UserInfo userInfo = users.getUserInfo(userID);
        if (userInfo.state == UserInfo.State.OFFLINE)
            throw new RuntimeException("User offline");
        if (userInfo.state == UserInfo.State.PLAYING)
            throw new RuntimeException("User playing");
        if (userInfo.adminGameID != null)
            throw new RuntimeException("User created another game");
        UUID gameID = UUID.randomUUID();
        userInfo.adminGameID = gameID;
        games.put(gameID, GameFactory.createGame(userID));
        return gameID;
    }

    public synchronized void deleteGame(UUID userID) throws RuntimeException {
        UserInfo userInfo = users.getUserInfo(userID);
        if (userInfo.adminGameID == null)
            throw new RuntimeException("User hasn't created a game");
        games.remove(userInfo.adminGameID);
        userInfo.adminGameID = null;
    }

    public synchronized ArrayList<UUID> listGame() throws RuntimeException {
        return new ArrayList<>(games.keySet());
    }

    public synchronized GameInfo getGameInfo(UUID gameID) throws RuntimeException {
        GameInfo gameInfo = games.getOrDefault(gameID, null).getGameInfo();
        if (gameInfo == null)
            throw new RuntimeException("Invalid user ID");
        return gameInfo;
    }

    public synchronized String getStatus() {
        return String.format("%d game(s) created.\n", games.size());
    }
}
