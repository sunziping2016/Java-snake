package client.view;

import common.controller.GameStateObserver;
import common.controller.PlayerActionConsumer;

import java.util.Observable;

/**
 * Created by sun on 4/27/16.
 *
 * Main view of the game, also a game observer, call repaint when necessary.
 */

public class GameView implements GameStateObserver {
    private PlayerActionConsumer consumer;

    @Override
    public void update(Observable o, Object arg) {

    }
}
