package server.controller;

import server.model.Games;
import server.model.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * Created by sun on 4/28/16.
 *
 * Game server.
 */
public class GameServer implements Runnable {
    private int port;
    private Users users;
    private Games games;
    private ServerSocket serverSocket;
    private Thread thread;

    public GameServer(int port, Users users) {
        this.port = port;
        this.users = users;
        games = new Games(users);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true)
                new CommandReceiver(serverSocket.accept(), users, games).start();
        } catch (SocketException error) {
            if (!error.getMessage().equals("Socket closed"))
                error.printStackTrace();
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            serverSocket = null;
        }
    }

    public void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        if (thread!=null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException error) {
                // Do nothing
            }
        }
    }

    public String getStatus() {
        return String.format("USERS SUMMARY:\n\t%sGAMES SUMMARY:\n\t%s", users.getStatus(), games.getStatus());
    }

    public boolean process(String command) {
        if (command.equals("exit"))
            return false;
        switch (command) {
            case "":
                // Do nothing
                break;
            case "status":
                System.out.print(getStatus());
                break;
            case "help":
                System.out.print(
                        "COMMANDS: \n" +
                        "  help              display this help message\n" +
                        "  status            display server status\n" +
                        "  exit              kill all the connections and exit\n");
                break;
            default:
                System.err.println("Unknown command. Type `help` to see list of commands.");
                break;
        }
        return true;
    }
}
