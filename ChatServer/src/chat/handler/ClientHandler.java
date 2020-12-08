package chat.handler;

import chat.MyServer;
import chat.auth.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private static final String AUTH_CMD_PREFIX = "/auth";
    private static final String AUTHOK_CMD_PREFIX = "/authok";
    private static final String AUTHERR_CMD_PREFIX = "/autherr";
    private static final String PRIVATE_MSG_PREFIX = "/w";
    private static final String END_CMD = "/end";
    private static final String CLIENT_MSG_PREFIX = "/clientMsg";
    private static final String SERVER_MSG_PREFIX = "/serverMsg";

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }).start();
    }

    private void authentication() throws IOException {
        String msg = in.readUTF();

        while (true) {
            if (msg.startsWith(AUTH_CMD_PREFIX)) {
                String[] parts = msg.split("\\s+", 3);
                String login = parts[1];
                String password = parts[2];

                AuthService authService = myServer.getAuthService();
                username = authService.getUsernameByLoginAndPassword(login, password);
                if (username != null) {
                    if (myServer.isUsernameBusy(username)) {
                        out.writeUTF(String.format("%s %s", AUTHERR_CMD_PREFIX, "Login already used!"));
                    }
                    out.writeUTF(String.format("%s %s", AUTHOK_CMD_PREFIX, username));
                    myServer.broadcastMessage(String.format(">>> %s connected to chat", username), this, true);
                    myServer.subscribe(this);
                    break;
                } else {
                    out.writeUTF(String.format("%s %s", AUTHERR_CMD_PREFIX, "Login or password is wrong!"));
                }
            } else {
                out.writeUTF(String.format("%s %s", AUTHERR_CMD_PREFIX, "Authorization error!"));
            }
        }
    }

    public String getUsername() {
        return username;
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            if (message.startsWith(END_CMD)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_PREFIX)) {
                String recipient = message.split("\\s+", 3)[1];
                myServer.personalMessage(message, recipient, this, false);
            } else {
                myServer.broadcastMessage(message, this, false);
            }
        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        if (sender == null) {
            out.writeUTF(String.format("%s %s", SERVER_MSG_PREFIX, message));
        } else {
            out.writeUTF(String.format("%s %s %s", CLIENT_MSG_PREFIX, sender, message));
        }
    }
}
