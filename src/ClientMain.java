import client.controller.CommandSender;

import java.net.Socket;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Main entry for Client.
 */
public class ClientMain {
    public static void main(String[] args) {
        /*MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

            }
        });*/
        try {
            CommandSender commandSender = new CommandSender(new Socket("localhost", 9797));
            //commandSender.userRegister("sun", "1998");
            commandSender.userLogin("sun", "1998");
            System.out.println(commandSender.userInfo(commandSender.getLoginId()));
            commandSender.createGame();
            //Thread.sleep(10000);
            System.out.println(commandSender.gameInfo(commandSender.listGame().get(0)));
            commandSender.deleteGame();
            commandSender.userLogout();
            commandSender.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
