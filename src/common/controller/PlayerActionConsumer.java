package common.controller;

import common.model.PlayerAction;

import java.util.function.Consumer;

/**
 * Created by sun on 4/27/16.
 *
 * Consume the player action.
 *
 * Implemented by `GameController` and `PlayerActionSender`.
 */
public interface PlayerActionConsumer extends Consumer<PlayerAction> {
}
