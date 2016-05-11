import common.controller.Preference;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
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

    public static void main(String[] args) {
        try {
            Preference.get().load(CONFIG_FILE);
            Users users = new Users();
            users.load(USER_FILE);

            String str = Preference.get().getProperty("port", null);
            int port = str == null ? DEFAULT_PORT : Integer.parseInt(str);

            GameServer gameServer = new GameServer(port, users);
            gameServer.start();

            System.out.println("Type `exit` to stop the server. (type `help` to see list of commands)");
            ConsoleReader console = new ConsoleReader();
            console.addCompleter(new StringsCompleter(gameServer.getCommands()));
            console.setPrompt("> ");
            String line;
            while ((line = console.readLine()) != null)
                if (!gameServer.process(line))
                    break;
            if (line == null)
                gameServer.exit();
            gameServer.stop();

            users.save(USER_FILE);
            Preference.get().save(CONFIG_FILE, CONFIG_FILE_COMMENT);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
