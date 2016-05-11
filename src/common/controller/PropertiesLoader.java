package common.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by sun on 4/28/16.
 *
 * Load properties resource.
 */
public class PropertiesLoader {
    private Properties properties = new Properties();

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void load(String filename) {
        try (InputStream input = PropertiesLoader.class.getResourceAsStream("/properties/" + filename)) {
            properties.load(input);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
    protected PropertiesLoader() { }

    private static PropertiesLoader propertiesLoader = new PropertiesLoader();

    public static PropertiesLoader get() {
        return propertiesLoader;
    }
}
