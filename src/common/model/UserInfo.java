package common.model;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Class represents the information of a user.
 */
public class UserInfo implements Serializable {

    public enum State {
        ONLINE,
        PLAYING,
        OFFLINE,
    }

    public String name;

    public State state;
    public UUID userID;
    public UUID gameID, adminGameID;             // none when user is not playing.
    public SocketAddress address;   // none when user is offline.

    public UserInfo(String name, State state, UUID userID, UUID gameID, SocketAddress address) {
        this.name = name;
        this.state = state;
        this.userID = userID;
        this.gameID = gameID;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        return name.equals(userInfo.name) && state == userInfo.state &&
                userID.equals(userInfo.userID) && gameID.equals(userInfo.gameID) && address.equals(userInfo.address);

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", userID=" + userID +
                ", gameID=" + gameID +
                ", adminGameID=" + adminGameID +
                ", address=" + address +
                '}';
    }
}
