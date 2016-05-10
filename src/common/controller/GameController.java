package common.controller;

import common.model.GameInfo;
import common.model.GameState;
import common.model.PlayerAction;

import java.util.ArrayList;
import java.util.Observable;
import java.util.UUID;
import java.util.function.Consumer;

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
            notifyObservers(null);
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

    public void exitAllObservers() {
        notifyObservers(null);
    }

    @Override
    public void accept(PlayerAction playerAction) {

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
