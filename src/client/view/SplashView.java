package client.view;

import client.controller.CommandSender;
import client.controller.GamePlayClient;
import common.model.GameState;

import java.awt.*;
import java.io.EOFException;
import java.io.StreamCorruptedException;
import java.net.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sun on 6/9/16.
 *
 * Splash View
 */
public class SplashView extends View {
    private static Color BACKGROUND_COLOR = Colors.get("SplashView.Background", Colors.GAME_BACKGROUND);

    public SplashView() {
        super("splash");
    }

    private CommandSender commandSender = null;
    private GamePlayClient gamePlayClient = null;

    @Override
    public void onStart(Content content) {
        if (content.getString("method", "").equals("pop") && commandSender != null && gamePlayClient != null) {
            System.out.println("stopping");
            gamePlayClient.join();
            System.out.println("stopped");
            if (commandSender.userInfo(commandSender.getLoginId()).adminGameID != null)
                commandSender.deleteGame();
            commandSender.userLogout();
            commandSender.close();
            commandSender = null;
            gamePlayClient = null;
        }
    }
    @Override
    public void onStop() {

    }

    @Override
    public void onPaint(Graphics g) {
        GraphicsWrapper g2 = new GraphicsWrapper(g, getWidth(), getHeight());
        g2.fillAll(BACKGROUND_COLOR);
        g2.drawImage("logo.png", 3, 2);
        g2.drawString("Sun\'s Snake Game", 1, Color.ORANGE, 6, 4);
        g2.drawStringCentered("Welcome to snake game. Press <space> to connect to server.", 0.5f, Color.CYAN, 8, 9);
    }

    @Override
    public void onKey(int keyCode) {
        if (keyCode == ' ') {
            CommandSender[] commandSender = new CommandSender[1];
            LoginDialog loginDialog = new LoginDialog(getViewManager().getFrame());
            loginDialog.setHost("localhost");
            loginDialog.setPort("9797");
            loginDialog.setInformationColor(Color.RED);
            loginDialog.addActionListener(e -> {
                try {
                    if (loginDialog.getHost().isEmpty()) {
                        loginDialog.setInformation("Host must not be empty.");
                        return;
                    }
                    if (loginDialog.getPort().isEmpty()) {
                        loginDialog.setInformation("Port must not be empty.");
                        return;
                    }
                    if (loginDialog.getUsername().isEmpty()) {
                        loginDialog.setInformation("Username must not be empty.");
                        return;
                    }
                    if (loginDialog.getPassword().isEmpty()) {
                        loginDialog.setInformation("Password must not be empty.");
                        return;
                    }
                    if (loginDialog.isRegister() && !loginDialog.getPassword().equals(loginDialog.getPasswordConfirm())) {
                        loginDialog.setInformation("Password not match.");
                        return;
                    }
                    Socket socket = new Socket();
                    socket.setSoTimeout(1000);
                    socket.connect(new InetSocketAddress(loginDialog.getHost(), Integer.parseInt(loginDialog.getPort())), 1000);
                    socket.setSoTimeout(0);
                    commandSender[0] = new CommandSender(socket);
                    try {
                        if (loginDialog.isRegister())
                            commandSender[0].userRegister(loginDialog.getUsername(), loginDialog.getPassword());
                        commandSender[0].userLogin(loginDialog.getUsername(), loginDialog.getPassword());
                    } catch (RuntimeException error) {
                        commandSender[0].close();
                        throw error;
                    }
                    loginDialog.close();
                } catch (EOFException | StreamCorruptedException error) {
                    loginDialog.setInformation("Unrecognized host.");
                } catch (ConnectException error) {
                    if (error.getMessage().equals("Invalid argument"))
                        loginDialog.setInformation("Invalid host.");
                    else
                        loginDialog.setInformation(error.getMessage() + ".");
                } catch (SocketTimeoutException error) {
                    loginDialog.setInformation("Connection timeout.");
                } catch (UnknownHostException error) {
                    loginDialog.setInformation("Unknown host.");
                } catch (NumberFormatException error) {
                    loginDialog.setInformation("Invalid port.");
                } catch (IllegalArgumentException error) {
                    loginDialog.setInformation("Port out of range.");
                } catch (RuntimeException error) {
                    loginDialog.setInformation(error.getMessage());
                } catch (Exception error) {
                    loginDialog.setInformation(error.getClass().getName());
                }
            });
            loginDialog.setVisible(true);
            if (loginDialog.isCancelled())
                return;
            ArrayList<UUID> gameList = commandSender[0].listGame();
            UUID gameID = null;
            for (UUID game: gameList)
                if (commandSender[0].gameInfo(game).state == GameState.State.PREPAREING) {
                    gameID = game;
                    break;
                }
            int admin = 0;
            if (gameID == null) {
                gameID = commandSender[0].createGame();
                admin = 1;
            }
            GamePlayClient gamePlayClient = commandSender[0].joinGame(gameID);
            GameView gameView = new GameView(gamePlayClient);
            gamePlayClient.addObserver(gameView);
            gamePlayClient.start();
            this.commandSender = commandSender[0];
            this.gamePlayClient = gamePlayClient;
            getViewManager().pushView(gameView, new Content().putInt("admin", admin));
        }
    }
}
