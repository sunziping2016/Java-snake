import client.controller.CommandSender;
import client.controller.GamePlayClient;
import client.view.*;
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
            mainFrame.pushView(new SplashView(), new Content());
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
