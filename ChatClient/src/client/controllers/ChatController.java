package client.controllers;

import client.NetworkClient;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChatController {

    @FXML
    private TextField inputField;

    @FXML
    private ListView<String> listView;

    @FXML
    private ListView<String> chatsView;

    @FXML
    private Label usernameTitle;

    private final ObservableList<String> wordList = FXCollections.observableArrayList("Вы присоединились к чату!");

    private final ObservableList<String> chatList = FXCollections.observableArrayList("Smith");

    public Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {
        listView.setItems(wordList);
        chatsView.setItems(chatList);
    }



    @FXML
    public void sendMessage() {
        String msg = inputField.getText();
        if (msg.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Empty message");
            alert.setContentText("Message successfully was not sent :)");
            alert.showAndWait();
            return;
        } else {
            appendMessage("Я: " + msg);
        }
        inputField.clear();

        try {
            network.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            NetworkClient.showErrorMsg("Problems with connection", "Ошибка отправки сообщения", e.getMessage());
        }
    }


    public void appendMessage(String msg) {
        listView.getItems().add(msg);
    }


    @FXML
    public void exit() {
        System.exit(1);
    }

    @FXML
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Chat");
        alert.setContentText("Directed by Nik-Shalashov");
        alert.showAndWait();
        return;
    }


    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }
}
