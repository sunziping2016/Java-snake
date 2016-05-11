package client.controller;

import common.model.GameCommand;
import common.model.GameInfo;
import common.model.UserInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sun on 4/28/16.
 *
 * Submit user command.
 */
public class CommandSender {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    protected static Object CheckForException(Object object) {
        if (object instanceof RuntimeException)
            throw (RuntimeException) object;
        return object;
    }

    public CommandSender(Socket socket) {
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public UUID userLogin(String username, String password) throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.USER_LOGIN));
            outputStream.writeObject(username);
            outputStream.writeObject(password);
            return (UUID) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public void userRegister(String username, String password) throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.USER_REGISTER));
            outputStream.writeObject(username);
            outputStream.writeObject(password);
            CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public UserInfo userInfo(UUID userID) throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.USER_INFO));
            outputStream.writeObject(userID);
            return (UserInfo) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public void userLogout() throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.USER_LOGOUT));
            CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public UUID getLoginId() throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GET_LOGIN_ID));
            return (UUID) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public UUID createGame() throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GAME_CREATE));
            return (UUID) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public void deleteGame() throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GAME_DELETE));
            CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<UUID> listGame() throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GAME_LIST));
            return (ArrayList<UUID>) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public GameInfo gameInfo(UUID gameID) throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GAME_INFO));
            outputStream.writeObject(gameID);
            return (GameInfo) CheckForException(inputStream.readObject());
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public GamePlayClient joinGame(UUID gameID) throws RuntimeException {
        try {
            outputStream.writeObject(new GameCommand(GameCommand.Command.GAME_JOIN));
            outputStream.writeObject(gameID);
            CheckForException(inputStream.readObject());
            return new GamePlayClient(outputStream, inputStream);
        } catch (ClassNotFoundException | IOException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    public void close() {
        try {
            outputStream.writeObject(null);
            outputStream.close();
            inputStream.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
