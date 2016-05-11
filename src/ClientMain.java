import client.controller.CommandSender;
import client.controller.GamePlayClient;
import common.model.PlayerAction;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
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
            commandSender.userLogin("sun", "1998");
            UUID gameID = commandSender.createGame();
            GamePlayClient gamePlayClient = commandSender.joinGame(gameID);
            gamePlayClient.addObserver((o, arg) -> {
                System.out.println(arg);
            });
            gamePlayClient.start();
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.START));
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.UP));
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.PAUSE));
            Thread.sleep(10000);
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.EXIT));
            gamePlayClient.join();
            commandSender.deleteGame();
            commandSender.userLogout();
            commandSender.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
