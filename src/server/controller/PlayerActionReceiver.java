package server.controller;

import common.controller.PlayerActionConsumer;

import java.io.ObjectInputStream;

/**
 * Created by sun on 4/27/16.
 *
 * receive `PlayerAction` and forward it to a `PlayerActionConsumer`
 */
public class PlayerActionReceiver implements Runnable {
    private PlayerActionConsumer consumer;
    private ObjectInputStream inputStream;

    @Override
    public void run() {
        // Exit when getting null from inputStream.
    }
}
