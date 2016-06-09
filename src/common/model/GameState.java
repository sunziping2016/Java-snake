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
    public static class Pos implements Serializable {
        public int x, y;
        public Pos() {
            x = y = 0;
        }
        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pos pos = (Pos) o;

            return x == pos.x && y == pos.y;

        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
    public enum MapBlock {
        WALL,
        WALKABLE,
    }
    public enum State {
        PREPAREING,
        START,
        PAUSE,
        OVER,
        CLOSING,
    }
    public enum Orientation {
        RIGHT,
        DOWN,
        LEFT,
        UP,
    }
    public static final int [][]DIRECTIONS = new int[][] {
            new int[] {  1 ,  0 },
            new int[] {  0 ,  1 },
            new int[] { -1 ,  0 },
            new int[] {  0 , -1 },
    };
    public State state;
    public MapBlock[][] map;
    public HashMap<UUID, ArrayList<Pos>> players;
    public HashMap<UUID, Orientation> orientations;
    public HashMap<UUID, Integer> lengths;
    public HashMap<UUID, Integer> scores;
    public ArrayList<Pos> apples;

    public GameState(State state, MapBlock[][] map, HashMap<UUID, ArrayList<Pos>> players, HashMap<UUID, Orientation> orientations,
                     HashMap<UUID, Integer> lengths, HashMap<UUID, Integer> scores, ArrayList<Pos> apples) {
        this.state = state;
        this.map = map;
        this.players = players;
        this.orientations = orientations;
        this.lengths = lengths;
        this.scores = scores;
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
        players = (HashMap<UUID, ArrayList<Pos>>) objectInputStream.readObject();
        apples = (ArrayList<Pos>) objectInputStream.readObject();
        objectInputStream.close();
    }
}
