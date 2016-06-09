package common.controller;

import common.model.GameInfo;
import common.model.GameState;

import java.util.*;

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
        GameState.MapBlock[][] map;
        outer:
        while (true) {
            map = new GameState.MapBlock[height][width];
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
                map[y][x] = GameState.MapBlock.WALL;
                for (int j = 0; j < WALL_LENGTH; ++j) {
                    ArrayList<Integer> directions = new ArrayList<>();
                    for (int k = 0; k < GameState.DIRECTIONS.length; ++k) {
                        if (x + GameState.DIRECTIONS[k][0] < 0 || x + GameState.DIRECTIONS[k][0] >= MAP_WIDTH || y + GameState.DIRECTIONS[k][1] < 0 || y + GameState.DIRECTIONS[k][1] >= MAP_HEIGHT)
                            continue;
                        if (map[y + GameState.DIRECTIONS[k][1]][x + GameState.DIRECTIONS[k][0]] == GameState.MapBlock.WALKABLE)
                            directions.add(directions.size(), k);
                    }
                    if (directions.size() < 3)
                        break;
                    if (direction >= 0 && x + GameState.DIRECTIONS[direction][0] >= 0 && x + GameState.DIRECTIONS[direction][0] < MAP_WIDTH &&
                            y + GameState.DIRECTIONS[direction][1] >= 0 && y + GameState.DIRECTIONS[direction][1] < MAP_HEIGHT &&
                            map[y + GameState.DIRECTIONS[direction][1]][x + GameState.DIRECTIONS[direction][0]] == GameState.MapBlock.WALL)
                        break;
                    if (!directions.contains(direction) || Math.random() > 0.94)
                        direction = directions.get(rand.nextInt(directions.size()));
                    x += GameState.DIRECTIONS[direction][0];
                    y += GameState.DIRECTIONS[direction][1];
                    if (map[y][x] == GameState.MapBlock.WALL)
                        break;
                    map[y][x] = GameState.MapBlock.WALL;
                }
            }
            // Check connectivity
            GameState.MapBlock[][] connectivity = new GameState.MapBlock[MAP_HEIGHT][MAP_WIDTH];
            for (int i = 0; i < height; ++i)
                for (int j = 0; j < width; ++j)
                    if (map[i][j] != GameState.MapBlock.WALKABLE)
                        connectivity[i][j] = GameState.MapBlock.WALL;
                    else
                        connectivity[i][j] = GameState.MapBlock.WALKABLE;
            int x, y;
            do {
                x = rand.nextInt(MAP_WIDTH);
                y = rand.nextInt(MAP_HEIGHT);
            } while(connectivity[y][x] != GameState.MapBlock.WALKABLE);
            ArrayDeque<GameState.Pos> queue = new ArrayDeque<>();
            queue.push(new GameState.Pos(x, y));
            while (!queue.isEmpty()) {
                GameState.Pos pos = queue.pop();
                if (connectivity[pos.y][pos.x] == GameState.MapBlock.WALL)
                    continue;
                connectivity[pos.y][pos.x] = GameState.MapBlock.WALL;
                for (int i = 0; i < GameState.DIRECTIONS.length; ++i) {
                    int newX = pos.x + GameState.DIRECTIONS[i][0];
                    int newY = pos.y + GameState.DIRECTIONS[i][1];
                    if (newX >= 0 && newX < MAP_WIDTH && newY >=0 && newY < MAP_HEIGHT && connectivity[newY][newX] != GameState.MapBlock.WALL)
                        queue.push(new GameState.Pos(newX, newY));
                }
            }
            for (int i = 0; i < height; ++i)
                for (int j = 0; j < width; ++j)
                    if (connectivity[i][j] == GameState.MapBlock.WALKABLE)
                        continue outer;
            break;
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
