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
        games.get(userInfo.adminGameID).exit();
        games.remove(userInfo.adminGameID);
        userInfo.adminGameID = null;
    }

    public synchronized void exit() {
        games.forEach(((gameID, gameController) -> {
            gameController.exit();
        }));
    }

    public synchronized ArrayList<UUID> listGame() throws RuntimeException {
        return new ArrayList<>(games.keySet());
    }

    public synchronized GameController getGameController(UUID gameID) throws RuntimeException {
        GameController gameController = games.getOrDefault(gameID, null);
        if (gameController == null)
            throw new RuntimeException("Invalid user ID");
        return gameController;
    }

    public synchronized GameInfo getGameInfo(UUID gameID) throws RuntimeException {
        return getGameController(gameID).getGameInfo();
    }

    public synchronized GameController joinGame(UUID userID, UUID gameID) throws RuntimeException {
        UserInfo userInfo = users.getUserInfo(userID);
        if (userInfo.state == UserInfo.State.OFFLINE)
            throw new RuntimeException("User offline");
        if (userInfo.state == UserInfo.State.PLAYING)
            throw new RuntimeException("User playing");
        if (userInfo.adminGameID != null && !userInfo.adminGameID.equals(gameID))
            throw new RuntimeException("Admin cannot join the other games");
        GameController gameController = getGameController(gameID);
        userInfo.state = UserInfo.State.PLAYING;
        userInfo.gameID = gameID;
        return gameController;
    }

    public synchronized void leaveGame(UUID userID) throws RuntimeException {
        UserInfo userInfo = users.getUserInfo(userID);
        if (userInfo.state == UserInfo.State.OFFLINE)
            throw new RuntimeException("User offline");
        if (userInfo.state == UserInfo.State.ONLINE)
            throw new RuntimeException("User not playing");
        userInfo.state = UserInfo.State.ONLINE;
        userInfo.gameID = null;

    }

    public synchronized String getStatus() {
        return String.format("%d game(s) created.\n", games.size());
    }
}
