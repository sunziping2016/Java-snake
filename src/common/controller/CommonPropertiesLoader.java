package common.controller;

/**
 * Created by sun on 4/28/16.
 *
 * Singleton to load shared properties.
 */
public class CommonPropertiesLoader extends PropertiesLoader {
    static {
        get().load("common.properties" );
    }
    public static PropertiesLoader get() {
        return PropertiesLoader.get();
    }
}
