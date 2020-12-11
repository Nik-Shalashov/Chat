package client.models;

import client.NetworkClient;
import client.controllers.ChatController;
import clientserver.Command;
import clientserver.commands.*;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class Network {

    private static final int SERVER_PORT = 8189;
    private static final String SERVER_HOST = "localhost";

    private final int port;
    private final String host;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Socket socket;
    private String username;

    public ObjectOutputStream getIn() {
        return out;
    }

    public ObjectInputStream getOut() {
        return in;
    }

    public Network() {
        this(SERVER_HOST, SERVER_PORT);
    }

    public Network(String serverHost, int serverPort) {
        this.host = serverHost;
        this.port = serverPort;
    }



    public boolean connect() {
        try {
            socket = new Socket(host, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            return true;

        } catch (IOException e) {
            NetworkClient.showErrorMsg("Fail connection process!", "", e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void waitMessage(ChatController chatController) {

        Thread thread = new Thread( () -> {
            try { while (true) {

                Command command = readCommand();
                if(command == null) {
                    NetworkClient.showErrorMsg("Error","Ошибка серверва", "Получена неверная команда");
                    continue;
                }

                switch (command.getType()) {
                    case INFO_MESSAGE: {
                        MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                        String message = data.getMessage();
                        String sender = data.getSender();
                        String formattedMessage = sender != null ? String.format("%s: %s", sender, message) : message;
                        Platform.runLater(() -> {
                            chatController.appendMessage(formattedMessage);
                        });
                        break;
                    }
                    case ERROR: {
                        ErrorCommandData data = (ErrorCommandData) command.getData();
                        String errorMessage = data.getErrorMessage();
                        Platform.runLater(() -> {
                            NetworkClient.showErrorMsg("Error", "Server error", errorMessage);
                        });
                        break;
                    }
                    case UPDATE_USERS_LIST: {
                        UpdateUsersListCommandData data = (UpdateUsersListCommandData) command.getData();
                        Platform.runLater(() -> chatController.updateUsers(data.getUsers()));
                        break;
                    }
                    default:
                        Platform.runLater(() -> {
                            NetworkClient.showErrorMsg("Error","Unknown command from server!", command.getType().toString());
                        });
                }

            }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Соединение потеряно!");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            out.writeObject(authCommand);

            Command command = readCommand();
            if (command == null) {
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();
                    return null;
                }

                case AUTH_ERROR:
                case ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    return "Unknown type of command: " + command.getType();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(Command.publicMessageCommand(username, message));
    }

    public void sendMessage(Command command) throws IOException {
        out.writeObject(command);
    }

    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command = Command.privateMessageCommand(recipient, message);
        sendMessage(command);
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }
}