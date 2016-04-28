package client.controller;

import common.controller.CommonPropertiesLoader;
import common.controller.PropertiesLoader;

/**
 * Created by sun on 4/28/16.
 *
 * Client specific properties.
 */
public class ClientPropertiesLoader extends CommonPropertiesLoader {
    static {
        get().load("client.properties");
    }
    public static PropertiesLoader get() {
        return CommonPropertiesLoader.get();
    }
}
