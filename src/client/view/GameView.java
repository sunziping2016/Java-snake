package client.view;

import common.controller.GameStateObserver;
import common.controller.PlayerActionConsumer;
import common.model.GameState;
import common.model.PlayerAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;


/**
 * Created by sun on 4/27/16.
 *
 * Main view of the game, also a game observer, call repaint when necessary.
 */

public class GameView extends View implements GameStateObserver {
    private static Color BACKGROUND_COLOR = Colors.get("GameView.Background", Colors.GAME_BACKGROUND);

    private PlayerActionConsumer consumer;
    private GameState gameState;

    public GameView(PlayerActionConsumer consumer) {
        super("game");
        this.consumer = consumer;
    }

    @Override
    public void update(Observable o, Object arg) {
        //System.out.println(arg);
        gameState = (GameState) arg;
        if (gameState == null)
            getViewManager().popView(new Content());
        else
            repaint();
    }

    @Override
    public void onPaint(Graphics g) {
        GraphicsWrapper g2 = new GraphicsWrapper(g, getWidth(), getHeight());
        g2.fillAll(BACKGROUND_COLOR);
        if (gameState != null) {
            GameState.MapBlock[][] map = gameState.map;
            final int width = map[0].length, height = map.length;
            float paddingLeft = 0f, paddingUp = 0f, blockSize;
            if (WIDTH / width > HEIGHT / height) {
                blockSize = HEIGHT / height;
                paddingLeft = (WIDTH - width * blockSize) / 2;
            } else {
                blockSize = WIDTH / width;
                paddingUp = (HEIGHT - height) / 2;
            }
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    final float x = paddingLeft + j * blockSize, y = paddingUp + i * blockSize;
                    Color color = Colors.GAME_BACKGROUND;
                    switch (map[i][j]) {
                        case WALL:
                            color = Colors.get("GameView.MapWall", Colors.GAME_MAP_WALL);
                            break;
                        case WALKABLE:
                            continue;
                        default:
                            break;
                    }
                    g2.fillCircle(x + blockSize / 2, y + blockSize / 2, blockSize / 2, color);
                }
            }
            Set<UUID> players = gameState.players.keySet();
            for (UUID player: players) {
                ArrayList<GameState.Pos> position = gameState.players.get(player);
                for (int j = 0; j < position.size(); ++j)
                    g2.fillCircle(paddingLeft + position.get(j).x * blockSize + blockSize / 2,
                            paddingUp + position.get(j).y * blockSize + blockSize / 2, blockSize / 2, Color.RED);
            }
            for (GameState.Pos apple: gameState.apples) {
                g2.fillCircle(paddingLeft + apple.x * blockSize + blockSize / 2,
                        paddingUp + apple.y * blockSize + blockSize / 2, blockSize / 2, Color.GREEN);
            }
        }
    }

    @Override
    public void onStart(Content content) {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onKey(int keyCode) {
        if (gameState == null) return;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                consumer.accept(new PlayerAction(PlayerAction.Action.UP));
                break;
            case KeyEvent.VK_DOWN:
                consumer.accept(new PlayerAction(PlayerAction.Action.DOWN));
                break;
            case KeyEvent.VK_LEFT:
                consumer.accept(new PlayerAction(PlayerAction.Action.LEFT));
                break;
            case KeyEvent.VK_RIGHT:
                consumer.accept(new PlayerAction(PlayerAction.Action.RIGHT));
                break;
            default:
                break;
        }
    }
}
