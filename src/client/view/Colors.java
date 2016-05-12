package client.view;

import client.controller.ClientPropertiesLoader;

import java.awt.*;

/**
 * Created by sun on 4/28/16.
 *
 * A tool set maintaining the theme of the game.
 */
public class Colors {
    public static Color get(String name, Color defaultColor) {
        String strColor = ClientPropertiesLoader.get().getProperty(name, null);
        if (strColor == null)
            return defaultColor;
        return new Color(Integer.parseUnsignedInt(strColor, 16), true);
    }

    public static Color mixtue(Color a, Color b, float r) {
        return new Color((int) (a.getRed() * r + b.getRed() * (1.0f - r) + 0.5),
                (int) (a.getGreen() * r + b.getGreen() * (1.0f - r) + 0.5),
                (int) (a.getBlue() * r + b.getBlue() * (1.0f - r) + 0.5),
                (int) (a.getAlpha() < b.getAlpha() ? a.getAlpha() : b.getAlpha()));
    }

    public static final Color FRAME_BACKGROUND = new Color(0x808080);

    public static final Color GAME_BACKGROUND = new Color(0x4E4E4E);
    public static final Color GAME_MAP_WALL = new Color(0xF4F4F4);
    public static final Color GAME_MAP_WALKABLE = new Color(0x6C6C6C);
    public static final Color GAME_MAP_NOTHING = new Color(0, true);
}
