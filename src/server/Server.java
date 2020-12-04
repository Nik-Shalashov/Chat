package server;

import client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static final int SERVER_PORT = 8189;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Waiting for connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection is ready!");

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            Thread waitMsg = new Thread(() -> {
                try {
                    while (true) {
                        String msg = input.readUTF();
                        System.out.println("Входящее сообщение: " + msg);

                        if (msg.equals("/exit")) {
                            System.out.println("Server shutdown!");
                            break;
                        }

                        output.writeUTF(msg);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Client.showErrorMsg("Connection lost", "", e.getMessage());
                }
            });

            Thread sendMsg = new Thread(() -> {
                try {
                    while (true) {
                        Scanner scanner = new Scanner(System.in);
                        String serverMsg = scanner.nextLine();
                        output.writeUTF("SERVER_INFO: " + serverMsg);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Client.showErrorMsg("Connection lost", "", e.getMessage());
                }
            });


            waitMsg.start();
            sendMsg.start();


        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Port is unavailable");
        }
    }

}
