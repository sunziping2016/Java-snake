package client.controller;

import common.controller.PlayerActionConsumer;
import common.model.PlayerAction;

import java.io.ObjectOutputStream;

/**
 * Created by sun on 4/27/16.
 *
 * Forward playerAction to the network.
 */
public class PlayerActionSender implements PlayerActionConsumer {
    private ObjectOutputStream outputStream;

    @Override
    public void accept(PlayerAction playerAction) {

    }
}
