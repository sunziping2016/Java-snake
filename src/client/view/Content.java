package client.view;

import java.util.HashMap;

/**
 * Created by sun on 4/28/16.
 *
 * Classes used to pass arguments between different view.
 */
public class Content {
    private HashMap<String, Object> objects = new HashMap<>();
    private HashMap<String, Integer> ints = new HashMap<>();
    private HashMap<String, String> strings = new HashMap<>();

    public Object getObject(String name, Object defaultValue) {
        return objects.getOrDefault(name, defaultValue);
    }
    public Integer getInt(String name, Integer defaultValue) {
        return ints.getOrDefault(name, defaultValue);
    }
    public String getString(String name, String defaultValue) {
        return strings.getOrDefault(name, defaultValue);
    }
    public Content putObject(String name, Object value) {
        objects.put(name, value);
        return this;
    }
    public Content putInt(String name, Integer value) {
        ints.put(name, value);
        return this;
    }
    public Content putString(String name, String value) {
        strings.put(name, value);
        return this;
    }
}
