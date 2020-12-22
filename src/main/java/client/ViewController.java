package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ViewController {

    @FXML
    private TextField inputField;

    @FXML
    private ListView<String> listView;

    @FXML
    private ListView<String> chatsView;

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
            //appendMessage(msg);
        }
        inputField.clear();

        try {
            network.getOut().writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Client.showErrorMsg("Problems with connection", "Ошибка отправки сообщения", e.getMessage());
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

}
