import client.controller.CommandSender;
import client.controller.GamePlayClient;
import client.view.Content;
import client.view.GameView;
import client.view.LoginDialog;
import client.view.MainFrame;
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
        try {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {

                }
            });

            CommandSender commandSender = new CommandSender(new Socket("localhost", 9797));
            commandSender.userLogin("sun", "1998");
            UUID gameID = commandSender.createGame();
            GamePlayClient gamePlayClient = commandSender.joinGame(gameID);
            GameView gameView = new GameView(gamePlayClient);
            gamePlayClient.addObserver(gameView);
            mainFrame.pushView(gameView, new Content());
            gamePlayClient.start();
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.JOIN));
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.START));
            Thread.sleep(20000);
            //LoginDialog loginDialog = new LoginDialog(mainFrame, "sun", "1998");
            //loginDialog.setVisible(true);
            gamePlayClient.accept(new PlayerAction(PlayerAction.Action.PAUSE));
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
