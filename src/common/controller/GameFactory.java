package common.controller;

import common.controller.GameController;
import common.model.GameInfo;
import common.model.GameState;
import server.controller.ServerPropertiesLoader;
import server.model.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sun on 5/10/16.
 *
 * Factory for GameModel and GameInfo.
 */
public class GameFactory {
    private static final int MAP_WIDTH = Integer.parseInt(ServerPropertiesLoader.get().getProperty("GameFactory.MapWidth"));
    private static final int MAP_HEIGHT = Integer.parseInt(ServerPropertiesLoader.get().getProperty("GameFactory.MapHeight"));

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

    public static Games.InfoAndControl createGame(UUID userID) {
        UUID gameID = UUID.randomUUID();
        GameInfo gameInfo = new GameInfo(gameID, new ArrayList<>(), userID);
        GameState gameState = new GameState(GameState.State.PREPAREING,
                createMap(MAP_WIDTH, MAP_HEIGHT), new HashMap<>(), new ArrayList<>());
        GameController gameController = new GameController(gameState);
        return new Games.InfoAndControl(gameInfo, gameController);
    }
}
