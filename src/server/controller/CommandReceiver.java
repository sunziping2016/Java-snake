package server.controller;

import common.model.GameCommand;
import common.model.GameInfo;
import common.model.UserInfo;
import server.model.Games;
import server.model.Users;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Handles user request.
 */
public class CommandReceiver implements Runnable {
    private static final int TIMEOUT = Integer.parseInt(ServerPropertiesLoader.get().getProperty("CommandReceiver.Timeout", "0"));

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Thread thread;

    private Users users;
    private Games games;
    private UUID loginID;

    public CommandReceiver(Socket socket, Users users, Games games) {
        try {
            this.socket = socket;
            this.users = users;
            this.games = games;
            this.socket.setSoTimeout(TIMEOUT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public boolean process(GameCommand gameCommand) throws IOException, ClassNotFoundException {
        try {
            String username, password;
            UUID userID, gameID;
            UserInfo userInfo;
            GameInfo gameInfo;
            switch (gameCommand.command) {
                case USER_LOGIN:
                    username = (String) inputStream.readObject();
                    password = (String) inputStream.readObject();
                    if (loginID != null)
                        throw new RuntimeException("User hasn't logged out");
                    userID = users.login(username, password, socket.getRemoteSocketAddress());
                    outputStream.writeObject(userID);
                    loginID = userID;
                    break;
                case USER_LOGOUT:
                    if (loginID == null)
                        throw new RuntimeException("No user logged in");
                    users.logout(loginID);
                    outputStream.writeObject(null);
                    loginID = null;
                    break;
                case USER_REGISTER:
                    username = (String) inputStream.readObject();
                    password = (String) inputStream.readObject();
                    users.registerUser(username, password);
                    outputStream.writeObject(null);
                    break;
                case USER_INFO:
                    userID = (UUID) inputStream.readObject();
                    userInfo = users.getUserInfo(userID);
                    outputStream.writeUnshared(userInfo);
                    break;
                case GET_LOGIN_ID:
                    outputStream.writeObject(loginID);
                    break;
                case GAME_CREATE:
                    if (loginID == null)
                        throw new RuntimeException("No user logged in");
                    gameID = games.createGame(loginID);
                    outputStream.writeObject(gameID);
                    break;
                case GAME_DELETE:
                    if (loginID == null)
                        throw new RuntimeException("No user logged in");
                    games.deleteGame(loginID);
                    outputStream.writeObject(null);
                    break;
                case GAME_LIST:
                    outputStream.writeObject(games.listGame());
                    break;
                case GAME_INFO:
                    gameID = (UUID) inputStream.readObject();
                    gameInfo = games.getGameInfo(gameID);
                    outputStream.writeUnshared(gameInfo);
                    break;
                default:
                    System.err.println("Unknown Action");
                    break;
            }
        } catch (RuntimeException error) {
            outputStream.writeObject(error);
        }
        return true;
    }

    @Override
    public void run() {
        System.out.println("Client connected from " + socket.getRemoteSocketAddress());
        try {
            while (true) {
                GameCommand gameCommand = (GameCommand) inputStream.readObject();
                if (gameCommand == null || !process(gameCommand))
                    break;
            }
        } catch (EOFException error) {
            // Do nothing
        } catch (ClassNotFoundException | IOException error) {
            error.printStackTrace();
        } finally {
            System.out.println("Client disconnected from " + socket.getRemoteSocketAddress());
            try {
                socket.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        if (loginID != null) {
            UserInfo userInfo = users.getUserInfo(loginID);
            if (userInfo.adminGameID != null)
                games.deleteGame(loginID);
            if (userInfo.state != UserInfo.State.OFFLINE)
                users.logout(loginID);
        }
    }

    public void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread!=null && thread.isAlive())
            thread.interrupt();
    }

    public void join() {
        if (thread!=null && thread.isAlive()) {
            //thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Do nothing.
            }
        }
    }
}
