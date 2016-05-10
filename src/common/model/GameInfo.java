package common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Class represents the information of a room.
 */
public class GameInfo implements Serializable {
    public enum State {
        WAITING,  // Wait for users to join.
        STARTED,  // Game has already started.
        OVER,     // Game has stopped.
    }
    public State state;
    public UUID gameID;
    public ArrayList<UUID> userIDs;
    public ArrayList<UUID> admin;
}
