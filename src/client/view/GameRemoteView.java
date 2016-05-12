package client.view;

import common.controller.PlayerActionConsumer;

/**
 * Created by sun on 4/28/16.
 *
 * A remote view of the game.
 */
abstract public class GameRemoteView extends GameView {
    public GameRemoteView(PlayerActionConsumer consumer) {
        super(consumer);
    }
}
