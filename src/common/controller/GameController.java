package common.controller;

import common.model.GameState;
import common.model.PlayerAction;

import java.util.Observable;
import java.util.function.Consumer;

/**
 * Created by sun on 4/27/16.
 *
 * An independent thread to update the `GameState` class according to accepted `PlayerAction` and timed clock,
 * and then notify `GameStateObserver`.
 */
public class GameController extends GameStateObservable implements PlayerActionConsumer, Runnable {
    private GameState gameState;

    public void step() {

    }

    @Override
    public void run() {

    }

    @Override
    public void accept(PlayerAction playerAction) {

    }
}
