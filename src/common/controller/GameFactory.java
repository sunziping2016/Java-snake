package common.controller;

import common.model.GameInfo;
import common.model.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sun on 5/10/16.
 *
 * Factory for GameModel and GameInfo.
 */
public class GameFactory {
    private static final int MAP_WIDTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.MapWidth"));
    private static final int MAP_HEIGHT = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.MapHeight"));

    public static GameState.MapBlock[][] createMap(int width, int height) {
        GameState.MapBlock[][] map = new GameState.MapBlock[height][width];
        // Code to generate a map.
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                if (i == 0 || i == height - 1 || j == 0 || j == width - 1)
                    map[i][j] = GameState.MapBlock.WALL;
                else
                    map[i][j] = GameState.MapBlock.WALKABLE;
            }
        }
        return map;
    }

    public static GameController createGame(UUID userID) {
        UUID gameID = UUID.randomUUID();
        GameState gameState = new GameState(GameState.State.PREPAREING,
                createMap(MAP_WIDTH, MAP_HEIGHT), new HashMap<>(), new ArrayList<>());
        GameController gameController = new GameController(gameState, gameID, userID);
        return gameController;
    }
}
