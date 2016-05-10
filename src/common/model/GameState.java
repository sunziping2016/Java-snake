package common.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by sun on 4/27/16.
 *
 * A serializable and synchronized class used to store game state and transfer it over the network.
 *
 * player, map(wall), apple, gamestate, gameMessage
 */
public class GameState implements Serializable {
    public static class Pos {
        int x, y;
    }
    public enum MapBlock {
        NOTHING,
        WALL,
        WALKABLE,
    }
    public enum State {
        PREPAREING,
        START,
        PAUSE,
        OVER,
    }
    public State state;
    public MapBlock[][] map;
    public HashMap<UUID, ArrayList<ArrayList<Pos>>> players;
    public ArrayList<Pos> apples;

    public GameState(State state, MapBlock[][] map, HashMap<UUID, ArrayList<ArrayList<Pos>>> players, ArrayList<Pos> apples) {
        this.state = state;
        this.map = map;
        this.players = players;
        this.apples = apples;
    }

    // Compress the data for better performance.
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // default serialization
        oos.defaultWriteObject();

        // compress
        ByteArrayOutputStream byteArrayOutputStream = new  ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(byteArrayOutputStream));
        objectOutputStream.writeObject(state);
        objectOutputStream.writeObject(map);
        objectOutputStream.writeObject(players);
        objectOutputStream.writeObject(apples);
        objectOutputStream.close();

        oos.writeInt(byteArrayOutputStream.size());
        oos.write(byteArrayOutputStream.toByteArray());
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        // default deserialization
        ois.defaultReadObject();

        byte[] compValue = new byte[ois.readInt()];
        ois.readFully(compValue);

        // decompress
        ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compValue)));
        state = (State) objectInputStream.readObject();
        map = (MapBlock[][]) objectInputStream.readObject();
        players = (HashMap<UUID, ArrayList<ArrayList<Pos>>>) objectInputStream.readObject();
        apples = (ArrayList<Pos>) objectInputStream.readObject();
        objectInputStream.close();
    }
}
