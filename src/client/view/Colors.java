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

    public static final Color BACKGROUND = new Color(0x515242, true);
}
