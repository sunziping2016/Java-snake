package server.controller;

import common.controller.Preference;
import server.model.Games;
import server.model.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

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

    private final ArrayList<CommandReceiver> clients = new ArrayList<>();

    public GameServer(int port, Users users) {
        this.port = port;
        this.users = users;
        games = new Games(users);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed() && !thread.isInterrupted()) {
                CommandReceiver commandReceiver = new CommandReceiver(serverSocket.accept(), users, games);
                synchronized (clients) {
                    clients.add(commandReceiver);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Integer.parseInt(Preference.get().getProperty("loglevel", "0")) <= 0)
                            System.out.println("Client connected from " + commandReceiver.getSocket().getRemoteSocketAddress());
                        commandReceiver.start();
                        commandReceiver.join();
                        synchronized (clients) {
                            clients.remove(commandReceiver);
                        }
                        if (Integer.parseInt(Preference.get().getProperty("loglevel", "0")) <= 0)
                            System.out.println("Client disconnected from " + commandReceiver.getSocket().getRemoteSocketAddress());
                    }
                }).start();
            }
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
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException error) {
                // Do nothing
            }
        }
    }

    public String getStatus() {
        return String.format("CONNECTIONS SUMMARY:\n" +
                "\t%d connections alive.\n" +
                "USERS SUMMARY:\n" +
                "\t%s" +
                "GAMES SUMMARY:\n" +
                "\t%s", clients.size(), users.getStatus(), games.getStatus());
    }

    public void exit() {
        if (!clients.isEmpty())
            System.out.println("Waiting for all connection to be closed...");
    }

    public ArrayList<String> getCommands() {
        return new ArrayList<String>(Arrays.asList(new String[] {
                "kill_all",
                "exit",
                "force_exit",
                "status",
                "quiet",
                "verbose",
                "help",
        }));
    }

    public boolean process(String command) {
        command = command.trim();
        switch (command) {
            case "":
                // Do nothing
                break;
            case "kill_all":
                games.exit();
                synchronized (clients) {
                    clients.forEach(CommandReceiver::stop);
                }
                break;
            case "exit":
                exit();
                return false;
            case "force_exit":
                games.exit();
                synchronized (clients) {
                    clients.forEach(CommandReceiver::stop);
                }
                return false;
            case "status":
                System.out.print(getStatus());
                break;
            case "quiet":
                Preference.get().setProperty("loglevel", "1");
                break;
            case "verbose":
                Preference.get().setProperty("loglevel", "0");
                break;
            case "help":
                System.out.print(
                        "COMMANDS: \n" +
                        "  help              display this help message\n" +
                        "  status            display server status\n" +
                        "  kill_all          kill all connections\n" +
                        "  verbose           set log level to verbose\n" +
                        "  quiet             set log level to quiet\n" +
                        "  exit              exit\n" +
                        "  force_exit        kill all connections and exit\n");
                break;
            default:
                System.err.println("Unknown command. Type `help` to see list of commands.");
                break;
        }
        return true;
    }
}
