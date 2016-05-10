package common.controller;

import java.io.*;
import java.util.Properties;

/**
 * Created by sun on 4/28/16.
 *
 * Preferences loaded from file.
 */
public class Preference {
    private Properties properties = new Properties();

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void load(String filename) {
        try (InputStream input = new FileInputStream(filename)) {
            properties.load(input);
        } catch (FileNotFoundException error) {
            // Do nothing.
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
    public void save(String filename, String comment) {
        try (OutputStream output = new FileOutputStream(filename)) {
            properties.store(output, comment);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
    private Preference() { }

    private static Preference preference = new Preference();

    public static Preference get() {
        return preference;
    }
}
