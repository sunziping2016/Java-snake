package server.controller;

import common.controller.GameController;
import common.controller.GameStateObserver;
import common.model.GameState;
import common.model.PlayerAction;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.Observable;
import java.util.UUID;

/**
 * Created by sun on 5/11/16.
 *
 * Forward `GameState` and `PlayerAction`.
 */
public class GamePlayServer implements GameStateObserver, Runnable{
    private GameController gameController;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private UUID userID;
    private Thread thread = new Thread(this);
    private boolean alive = true;

    public GamePlayServer(GameController gameController, ObjectOutputStream outputStream, ObjectInputStream inputStream, UUID userID) {
        this.gameController = gameController;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.userID = userID;
    }

    @Override
    synchronized public void update(Observable o, Object arg) {
        // assert arg instanceof GameState;
        if (!alive) return;
        GameState gameState = (GameState) arg;
        try {
            if (arg == null)
                alive = false;
            //System.out.println(gameState);
            outputStream.writeUnshared(gameState);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    @Override
    public void run() {
        gameController.addObserver(this);
        try {
            while (true) {
                PlayerAction playerAction = (PlayerAction) inputStream.readObject();

                playerAction.userID = userID;
                gameController.accept(playerAction);
                if (playerAction.action == PlayerAction.Action.EXIT) {
                    if (alive) {
                        update(gameController, null);
                        alive = false;
                    }
                    break;
                }
            }
        } catch (EOFException | SocketException error) {
            // Do nothing
        } catch (IOException | ClassNotFoundException error) {
            error.printStackTrace();
        } finally {
            gameController.deleteObserver(this);
        }
    }

    public void start() {
        thread.start();
    }

    public void join() {
        try {
            thread.join();
        } catch (InterruptedException error) {
            // Do nothing
        }
    }
}
