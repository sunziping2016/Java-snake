package common.controller;

import common.model.GameInfo;
import common.model.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sun on 5/10/16.
 *
 * Factory for GameModel and GameInfo.
 */
public class GameFactory {
    private static final int MAP_WIDTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.MapWidth"));
    private static final int MAP_HEIGHT = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.MapHeight"));
    private static final int WALL_COUNTS = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.WallCount"));
    private static final int WALL_LENGTH = Integer.parseInt(CommonPropertiesLoader.get().getProperty("GameFactory.WallLength"));

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
        Random rand = new Random();
        for (int i = 0; i < WALL_COUNTS; ++i) {
            int x = rand.nextInt(MAP_WIDTH), y = rand.nextInt(MAP_HEIGHT);
            int direction = -1;
            int [][]dir = new int[][] {
                    new int[] {  1 ,  0 },
                    new int[] {  0 ,  1 },
                    new int[] { -1 ,  0 },
                    new int[] {  0 , -1 },
            };
            map[y][x] = GameState.MapBlock.WALL;
            for (int j = 0; j < WALL_LENGTH; ++j) {
                ArrayList<Integer> directions = new ArrayList<>();
                for (int k = 0; k < dir.length; ++k) {
                    if (x + dir[k][0] < 0 || x + dir[k][0] >= MAP_WIDTH || y + dir[k][1] < 0 || y + dir[k][1] >= MAP_HEIGHT)
                        continue;
                    if (map[y + dir[k][1]][x + dir[k][0]] == GameState.MapBlock.WALKABLE)
                        directions.add(directions.size(), k);
                }
                if (directions.size() < 3)
                    break;
                if (direction >= 0 && x + dir[direction][0] >= 0 && x + dir[direction][0] < MAP_WIDTH &&
                        y + dir[direction][1] >= 0 && y + dir[direction][1] < MAP_HEIGHT &&
                        map[y + dir[direction][1]][x + dir[direction][0]] == GameState.MapBlock.WALL)
                    break;
                if (!directions.contains(direction) || Math.random() > 0.9)
                    direction = directions.get(rand.nextInt(directions.size()));
                x += dir[direction][0];
                y += dir[direction][1];
                if (map[y][x] == GameState.MapBlock.WALL)
                    break;
                map[y][x] = GameState.MapBlock.WALL;
            }
        }
        return map;
    }

    public static GameController createGame(UUID userID) {
        UUID gameID = UUID.randomUUID();
        GameState gameState = new GameState(GameState.State.PREPAREING, createMap(MAP_WIDTH, MAP_HEIGHT),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>());
        GameController gameController = new GameController(gameState, gameID, userID);
        return gameController;
    }
}
