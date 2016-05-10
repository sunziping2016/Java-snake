import common.controller.Preference;
import server.controller.GameServer;
import server.controller.ServerPropertiesLoader;
import server.model.Users;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by sun on 4/28/16.
 *
 * Main class for server.
 */
public class ServerMain {
    private static final String CONFIG_FILE = ServerPropertiesLoader.get().getProperty("ServerMain.ConfigFile");
    private static final String CONFIG_FILE_COMMENT = ServerPropertiesLoader.get().getProperty("ServerMain.ConfigFileComment");
    private static final String USER_FILE = ServerPropertiesLoader.get().getProperty("ServerMain.UserFile");
    private static final int DEFAULT_PORT = Integer.parseInt(ServerPropertiesLoader.get().getProperty("ServerMain.DefaultPort"));

    public static void main(String[] args) throws IOException, InterruptedException {
        Preference.get().load(CONFIG_FILE);
        Users users = new Users();
        users.load(USER_FILE);

        String str = Preference.get().getProperty("port", null);
        int port = str == null ? DEFAULT_PORT : Integer.parseInt(str);

        GameServer gameServer = new GameServer(port, users);
        gameServer.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type `exit` to stop the server. (type `help` to see list of commands)");
        do {
            System.out.print("> ");
        } while (scanner.hasNextLine() && gameServer.process(scanner.nextLine()));

        gameServer.stop();

        users.save(USER_FILE);
        Preference.get().save(CONFIG_FILE, CONFIG_FILE_COMMENT);
    }
}
