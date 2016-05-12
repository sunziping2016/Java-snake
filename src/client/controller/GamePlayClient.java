package client.controller;

import common.controller.GameStateObservable;
import common.controller.PlayerActionConsumer;
import common.model.GameState;
import common.model.PlayerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by sun on 4/27/16.
 *
 * Receive the `GameState` and notify `GameStateObserver` and deliver `PlayerAction`.
 */

public class GamePlayClient extends GameStateObservable implements Runnable, PlayerActionConsumer {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Thread thread = new Thread(this);
    private boolean alive = true;

    public GamePlayClient(ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
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

    @Override
    public void run() {
        try {
            while (true) {
                GameState gameState = (GameState) inputStream.readObject();
                setChanged();
                notifyObservers(gameState);
                if (gameState == null) {
                    if (alive) {
                        accept(new PlayerAction(PlayerAction.Action.EXIT));
                        alive = false;
                    }
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException error) {
            error.printStackTrace();
        }
    }

    @Override
    synchronized public void accept(PlayerAction playerAction) {
        if (!alive) return;
        try {
            outputStream.writeObject(playerAction);
            if (playerAction.action == PlayerAction.Action.EXIT)
                alive = false;
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
