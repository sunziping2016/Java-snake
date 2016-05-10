package server.model;

import common.model.UserInfo;
import javafx.scene.shape.Path;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * GameServer side user management.
 */
public class Users {
    static private class PasswordAndID {
        String password;
        UUID userID;

        PasswordAndID(String password, UUID userID) {
            this.password = password;
            this.userID = userID;
        }
    }

    private HashMap<String, PasswordAndID> registeredUsers = new HashMap<>();
    private HashMap<UUID, UserInfo> users = new HashMap<>();

    public synchronized void load(String filename) {
        try {
            Files.lines(Paths.get(filename)).forEach(line -> {
                String[] words = line.split("\t");
                String username = words[0], password = words[1];
                UUID userID = UUID.fromString(words[2]);
                registeredUsers.put(username, new PasswordAndID(password, userID));
                users.put(userID, new UserInfo(username, UserInfo.State.OFFLINE, userID, null, null));
            });
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public synchronized void save(String filename) {
        try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
            for (String name: registeredUsers.keySet()) {
                PasswordAndID passwordAndID = registeredUsers.get(name);
                writer.println(name + '\t' + passwordAndID.password + '\t' + passwordAndID.userID);
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public synchronized UUID login(String username, String password, SocketAddress address) throws RuntimeException {
        UUID userID = getUserID(username, password);
        UserInfo userInfo = getUserInfo(userID);
        if (userInfo.state != UserInfo.State.OFFLINE)
            throw new RuntimeException("User already logged in");
        userInfo.state = UserInfo.State.ONLINE;
        userInfo.address = address;
        return userID;
    }

    public synchronized void logout(UUID userID) throws RuntimeException {
        UserInfo userInfo = getUserInfo(userID);
        if (userInfo.state == UserInfo.State.PLAYING)
            throw new RuntimeException("User playing");
        if (userInfo.state == UserInfo.State.OFFLINE)
            throw new RuntimeException("User already logged out");
        if (userInfo.adminGameID != null)
            throw new RuntimeException("User has created a game");
        userInfo.state = UserInfo.State.OFFLINE;
        userInfo.address = null;
    }

    public synchronized UUID registerUser(String username, String password) throws RuntimeException {
        if (username.contains("\t") || username.contains("\n"))
            throw new RuntimeException("Invalid character in username");
        if (password.contains("\t") || username.contains("\n"))
            throw new RuntimeException("Invalid character in password");
        if (registeredUsers.getOrDefault(username, null) != null)
            throw new RuntimeException("User already registered");
        UUID userID = UUID.randomUUID();
        registeredUsers.put(username, new PasswordAndID(password, userID));
        users.put(userID, new UserInfo(username, UserInfo.State.OFFLINE, userID, null, null));
        return userID;
    }

    public synchronized UUID getUserID(String username, String password) throws RuntimeException {
        if (username.contains("\t") || username.contains("\n"))
            throw new RuntimeException("Invalid character in username");
        if (password.contains("\t") || username.contains("\n"))
            throw new RuntimeException("Invalid character in password");
        PasswordAndID passwordAndID = registeredUsers.getOrDefault(username, null);
        if (passwordAndID == null || !passwordAndID.password.equals(password))
            throw new RuntimeException("Username or password is wrong");
        return passwordAndID.userID;
    }

    public synchronized UserInfo getUserInfo(UUID userID) throws RuntimeException {
        UserInfo userInfo = users.getOrDefault(userID, null);
        if (userInfo == null)
            throw new RuntimeException("Invalid user ID");
        return userInfo;
    }

    public synchronized int countOnline() {
        final int[] count = new int[1];
        users.forEach((userID, userInfo) -> {
            if (userInfo.state != UserInfo.State.OFFLINE)
                ++count[0];
        });
        return count[0];
    }

    public synchronized String getStatus() {
        return String.format("%d user(s) loaded\t%d user(s) online.\n", users.size(), countOnline());
    }
}
