package common.controller;

import common.model.GameInfo;
import common.model.GameState;
import common.model.PlayerAction;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sun on 4/27/16.
 *
 * An independent thread to update the `GameState` class according to accepted `PlayerAction` and timed clock,
 * and then notify `GameStateObserver`.
 */
public class GameController extends GameStateObservable implements PlayerActionConsumer, Runnable {
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

    public void step() {

    }

    @Override
    public void run() {
        //while ()
        while (true) {
            try {
                Thread.sleep(2000);
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
        ArrayList<GameState.Pos> player = gameState.players.getOrDefault(playerAction.userID, null);
        //System.out.println(playerAction);
        switch (playerAction.action) {
            case START:
                if (gameState.state == GameState.State.PAUSE || gameState.state == GameState.State.PREPAREING) {
                    gameState.state = GameState.State.START;
                    start();
                }
                break;
            case JOIN:
                ArrayList<GameState.Pos> position = new ArrayList<>();
                position.add(new GameState.Pos(5, 5));
                gameState.players.put(playerAction.userID, position);
                setChanged();
                break;
            case EXIT:
                if (gameState.players.containsKey(playerAction.userID)) {
                    gameState.players.remove(playerAction.userID);
                    setChanged();
                }
            case PAUSE:
                if (gameState.state == GameState.State.PAUSE) {
                    gameState.state = GameState.State.PAUSE;
                    stop();
                }
                break;
            case UP:
                if (player != null) {
                    --player.get(0).y;
                    setChanged();
                }
                break;
            case DOWN:
                if (player != null) {
                    ++player.get(0).y;
                    setChanged();
                }
                break;
            case LEFT:
                if (player != null) {
                    --player.get(0).x;
                    setChanged();
                }
                break;
            case RIGHT:
                if (player != null) {
                    ++player.get(0).x;
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
