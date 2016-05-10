package server.controller;

import common.controller.CommonPropertiesLoader;
import common.controller.PropertiesLoader;

/**
 * Created by sun on 4/28/16.
 *
 * GameServer specific properties.
 */
public class ServerPropertiesLoader extends CommonPropertiesLoader {
    static {
        get().load("server.properties");
    }
    public static PropertiesLoader get() {
        return CommonPropertiesLoader.get();
    }
}
