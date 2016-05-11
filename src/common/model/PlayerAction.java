package common.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by sun on 4/27/16.
 *
 * A serializable class used to store player state and transfer it over the network.
 *
 * gameid, userid, action
 */
public class PlayerAction implements Serializable {
    public enum Action {
        JOIN,           // Add a player
        EXIT,           // Delete a player

        START,          // Admin required
        PAUSE,          // Admin required

        UP,             // Move around
        DOWN,
        LEFT,
        RIGHT,
    }

    public Action action;
    public UUID userID;

    public PlayerAction(Action action) {
        this.action = action;
        this.userID = null;
    }

    public PlayerAction(Action action, UUID userID) {
        this.action = action;
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "PlayerAction{" +
                "action=" + action +
                ", userID=" + userID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerAction that = (PlayerAction) o;

        return action == that.action && userID.equals(that.userID);
    }

    @Override
    public int hashCode() {
        int result = action.hashCode();
        result = 31 * result + userID.hashCode();
        return result;
    }
}
