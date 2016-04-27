package client.controller;

import common.controller.GameStateObservable;

import java.io.ObjectInputStream;

/**
 * Created by sun on 4/27/16.
 *
 * Receive the `GameState` and notify `GameStateObserver`.
 */

public class GameStateReceiver extends GameStateObservable implements Runnable {
    private ObjectInputStream inputStream;

    @Override
    public void run() {
        // notifyObservers(null) when getting null from inputStream;
    }
}
