import client.view.MainFrame;

/**
 * Created by sun on 4/28/16.
 *
 * Main entry for Client.
 */
public class ClientMain {
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

            }
        });
    }
}
