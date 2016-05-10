package common.model;

import common.controller.GameStateObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Class represents the information of a room.
 */
public class GameInfo implements Serializable {
    public GameState.State state;
    public UUID gameID;
    public ArrayList<UUID> userIDs;
    public UUID admin;

    public GameInfo(GameState.State state, UUID gameID, ArrayList<UUID> userIDs, UUID admin) {
        this.state = state;
        this.gameID = gameID;
        this.userIDs = userIDs;
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "state=" + state +
                ", gameID=" + gameID +
                ", userIDs=" + userIDs +
                ", admin=" + admin +
                '}';
    }
}
