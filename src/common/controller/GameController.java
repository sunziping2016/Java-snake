package common.controller;

import common.model.GameInfo;
import common.model.GameState;
import common.model.PlayerAction;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sun on 4/27/16.
 *
 * An independent thread to update the `GameState` class according to accepted `PlayerAction` and timed clock,
 * and then notify `GameStateObserver`.
 */
public class GameController extends GameStateObservable implements PlayerActionConsumer, Runnable {
    private static final int BORN_PADDING = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.BornPadding"));
    private static final int BORN_LENGTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.BornLength"));
    private static final int APPLE_LENGTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.AppleLength"));
    private static final int INTERVAL = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.TurnInterval"));
    private static final int BONUS_APPLE = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.BonusApple"));
    private static final int BONUS_TURN_BASE = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.BonusTurnBase"));
    private static final int BONUS_TURN_PER_LENGTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameController.BonusTurnPerLength"));

    private static final float APPLE_POSSIBILITY = Float.parseFloat(CommonPropertiesLoader.get().getProperty("GameController.ApplePossibility"));

    private static final float[][] BORN_POSITION = new float[][] {
            new float[] { 0.0f, 0.0f },
            new float[] { 1.0f, 1.0f },
            new float[] { 1.0f, 0.0f },
            new float[] { 0.0f, 1.0f },
            new float[] { 0.5f, 0.5f },
    };

    private GameState gameState;
    private Thread thread;

    private UUID gameID;
    private UUID adminID;
    private ArrayList<UUID> userIDs = new ArrayList<>();

    public GameController(GameState gameState, UUID gameID, UUID adminID) {
        this.gameState = gameState;
        this.gameID = gameID;
        this.adminID = adminID;
    }

    public void die(UUID player) {
        gameState.players.remove(player);
        gameState.orientations.remove(player);
        gameState.lengths.remove(player);
    }

    public void step(UUID player) {
        GameState.Pos pos = gameState.players.get(player).get(0);
        int x = pos.x, y = pos.y;
        x += GameState.DIRECTIONS[gameState.orientations.get(player).ordinal()][0];
        y += GameState.DIRECTIONS[gameState.orientations.get(player).ordinal()][1];
        if (gameState.map[y][x] != GameState.MapBlock.WALKABLE) {
            die(player);
            return;
        }
        for (ArrayList<GameState.Pos> others: gameState.players.values())
            if (others.contains(new GameState.Pos(x, y))) {
                die(player);
                return;
            }
        if (gameState.apples.contains(new GameState.Pos(x, y))) {
            gameState.apples.remove(new GameState.Pos(x, y));
            gameState.scores.replace(player, gameState.scores.get(player) + BONUS_APPLE);
            gameState.lengths.replace(player, gameState.lengths.get(player) + APPLE_LENGTH);
            if (gameState.apples.isEmpty())
                generateApple();
        }
        gameState.players.get(player).add(0, new GameState.Pos(x, y));
        if (gameState.players.get(player).size() > gameState.lengths.get(player))
            gameState.players.get(player).remove(gameState.players.get(player).size() - 1);
    }

    public void generateApple() {
        Random rand = new Random();
        int x, y;
        outer:
        while (true) {
            x = rand.nextInt(gameState.map[0].length);
            y = rand.nextInt(gameState.map.length);
            if (gameState.map[y][x] != GameState.MapBlock.WALKABLE)
                continue;
            for (ArrayList<GameState.Pos> others: gameState.players.values())
                if (others.contains(new GameState.Pos(x, y)))
                    continue outer;
            if (gameState.apples.contains(new GameState.Pos(x, y)))
                continue;
            break;
        }
        gameState.apples.add(gameState.apples.size(), new GameState.Pos(x, y));
    }

    @Override
    public void run() {
        //while ()
        while (true) {
            try {
                Thread.sleep(INTERVAL);
                ArrayList<UUID> players = new ArrayList<>();
                for (UUID player: gameState.players.keySet())
                    players.add(player);
                for (UUID player: players) {
                    step(player);
                    if (gameState.players.containsKey(player))
                        gameState.scores.replace(player, gameState.scores.get(player) +
                                BONUS_TURN_BASE + BONUS_TURN_PER_LENGTH * gameState.players.get(player).size());
                }
                if (Math.random() < APPLE_POSSIBILITY)
                    generateApple();
                setChanged();
                notifyObservers(gameState);
            } catch (InterruptedException error) {
                notifyObservers(gameState);
                break;
            }
        }
    }

    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    public void start() {
        if (!isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (isAlive()) {
            thread.interrupt();
        }
    }

    public void join() {
        if (isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Do nothing.
            }
        }
    }

    public void exit() {
        setChanged();
        notifyObservers(null);
        stop();
        join();
    }

    @Override
    public void accept(PlayerAction playerAction) {
        //System.out.println(playerAction);
        switch (playerAction.action) {
            case START:
                if (gameState.state == GameState.State.PAUSE || gameState.state == GameState.State.PREPAREING) {
                    gameState.state = GameState.State.START;
                    start();
                    generateApple();
                }
                break;
            case JOIN:
                ArrayList<GameState.Pos> position = new ArrayList<>();
                int width = gameState.map[0].length, height = gameState.map.length;
                int x = (int)(width * BORN_POSITION[gameState.players.size()][0] + 0.5);
                int y = (int)(height * BORN_POSITION[gameState.players.size()][1] + 0.5);
                if (x < BORN_PADDING)
                    x = BORN_PADDING;
                else if (x >= width - BORN_PADDING)
                    x = width - BORN_PADDING;
                if (y < BORN_PADDING)
                    y = BORN_PADDING;
                else if (y >= height - BORN_PADDING)
                    y = height - BORN_PADDING;
                if (gameState.map[y][x] != GameState.MapBlock.WALKABLE) {
                    int r = 1, newX = x , newY = y;
                    outer:
                    while (true) {
                        for (newX = x - r, newY = y - r; newX < x + r; ++newX)
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height && gameState.map[newY][newX] == GameState.MapBlock.WALKABLE)
                                break outer;
                        for (newX = x + r, newY = y - r; newY < y + r; ++newY)
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height && gameState.map[newY][newX] == GameState.MapBlock.WALKABLE)
                                break outer;
                        for (newX = x + r, newY = y + r; newX > x - r; --newX)
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height && gameState.map[newY][newX] == GameState.MapBlock.WALKABLE)
                                break outer;
                        for (newX = x - r, newY = y + r; newY > y - r; --newY)
                            if (newX >= 0 && newX < width && newY >= 0 && newY < height && gameState.map[newY][newX] == GameState.MapBlock.WALKABLE)
                                break outer;
                        ++r;
                    }
                    x = newX;
                    y = newY;
                }
                ArrayList<Integer> directions = new ArrayList<>();
                for (int k = 0; k < GameState.DIRECTIONS.length; ++k) {
                    if (x + GameState.DIRECTIONS[k][0] < 0 || x + GameState.DIRECTIONS[k][0] >= width || y + GameState.DIRECTIONS[k][1] < 0 || y + GameState.DIRECTIONS[k][1] >= height)
                        continue;
                    if (gameState.map[y + GameState.DIRECTIONS[k][1]][x + GameState.DIRECTIONS[k][0]] == GameState.MapBlock.WALKABLE)
                        directions.add(directions.size(), k);
                }
                position.add(new GameState.Pos(x, y));
                gameState.players.put(playerAction.userID, position);
                gameState.orientations.put(playerAction.userID,
                        GameState.Orientation.values()[directions.get(new Random().nextInt(directions.size()))]);
                gameState.lengths.put(playerAction.userID, BORN_LENGTH);
                gameState.scores.put(playerAction.userID, 0);
                setChanged();
                break;
            case EXIT:
                if (gameState.players.containsKey(playerAction.userID)) {
                    gameState.players.remove(playerAction.userID);
                    gameState.orientations.remove(playerAction.userID);
                    gameState.lengths.remove(playerAction.userID);
                    // Score reserved.
                    setChanged();
                }
            case PAUSE:
                if (gameState.state == GameState.State.PAUSE) {
                    gameState.state = GameState.State.PAUSE;
                    stop();
                }
                break;
            case UP:
                if (gameState.players.containsKey(playerAction.userID) && gameState.state == GameState.State.START) {
                    gameState.orientations.replace(playerAction.userID, GameState.Orientation.UP);
                    step(playerAction.userID);
                    setChanged();
                }
                break;
            case DOWN:
                if (gameState.players.containsKey(playerAction.userID) && gameState.state == GameState.State.START) {
                    gameState.orientations.replace(playerAction.userID, GameState.Orientation.DOWN);
                    step(playerAction.userID);
                    setChanged();
                }
                break;
            case LEFT:
                if (gameState.players.containsKey(playerAction.userID) && gameState.state == GameState.State.START) {
                    gameState.orientations.replace(playerAction.userID, GameState.Orientation.LEFT);
                    step(playerAction.userID);
                    setChanged();
                }
                break;
            case RIGHT:
                if (gameState.players.containsKey(playerAction.userID) && gameState.state == GameState.State.START) {
                    gameState.orientations.replace(playerAction.userID, GameState.Orientation.RIGHT);
                    step(playerAction.userID);
                    setChanged();
                }
                break;
            default:
                System.err.println("Unknown Player Action.");
                break;
        }
        notifyObservers(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public UUID getGameID() {
        return gameID;
    }

    public UUID getAdminID() {
        return adminID;
    }

    public ArrayList<UUID> getUserIDs() {
        return userIDs;
    }

    public GameInfo getGameInfo() {
        return new GameInfo(gameState.state, gameID, userIDs, adminID);
    }
}
