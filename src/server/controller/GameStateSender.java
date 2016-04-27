package server.controller;

import common.controller.GameStateObserver;

import java.io.ObjectOutputStream;
import java.util.Observable;

/**
 * Created by sun on 4/27/16.
 *
 * Send `GameState` over socket when it changes.
 */
public class GameStateSender implements GameStateObserver {
    private ObjectOutputStream outputStream;

    @Override
    public void update(Observable o, Object arg) {
        // put null to outputStream when arg is null.
    }
}
