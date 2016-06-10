package client.view;

/**
 * Created by Sun on 3/21/2016.
 *
 * Load image file.
 */

import client.controller.ClientPropertiesLoader;

import java.awt.Image;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.geom.*;

class ImageManager {
    private static final float REAL_DENSITY = 1.0f;

    private static HashMap<String, BufferedImage> cache = new HashMap<>();
    private static HashMap<String, Image> instanceCache = new HashMap<>();

    private static void load(String name) throws Exception {
        BufferedImage b = ImageIO.read(ImageManager.class.getResourceAsStream("/images/" + name));
        cache.put(name, b);
    }

    public static BufferedImage getImage(String name) {
        if (!cache.containsKey(name)) {
            try {
                load(name);
            } catch (Exception e) {
                return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            }
        }
        return cache.get(name);
    }

    public static float getDefaultScale(String name) {
        String val = ClientPropertiesLoader.get().getProperty("ImageManager.Scale." + name, "1.0");
        try {
            return Float.parseFloat(val);
        } catch (Exception e) {
            return 1.0f;
        }
    }
}