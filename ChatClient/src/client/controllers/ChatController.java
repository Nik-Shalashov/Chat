package client.controllers;

import client.NetworkClient;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ChatController {

    @FXML
    private TextField inputField;

    @FXML
    private TextArea messagesView;

    @FXML
    private ListView<String> chatsView;

    @FXML
    private Label usernameTitle;

    @FXML
    private Button sendButton;

    public Network network;
    private String selectedRecipient;

    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }
    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {
        sendButton.setOnAction(event -> ChatController.this.sendMessage());
        inputField.setOnAction(event -> ChatController.this.sendMessage());

        chatsView.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = chatsView.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                chatsView.requestFocus();
                if (! cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell ;
        });
    }




    public void sendMessage() {
        String msg = inputField.getText();

        if(msg.isBlank()) {
            return;
        }

        appendMessage("Я: " + msg);
        inputField.clear();

        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(msg, selectedRecipient);
            }
            else {
                network.sendMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
            NetworkClient.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }
    }


    public void appendMessage(String message) {
        String timestamp = DateFormat.getInstance().format(new Date());
        messagesView.appendText(timestamp);
        messagesView.appendText(System.lineSeparator());
        messagesView.appendText(message);
        messagesView.appendText(System.lineSeparator());
        messagesView.appendText(System.lineSeparator());
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



    public void updateUsers(List<String> users) {
        chatsView.setItems(FXCollections.observableArrayList(users));
    }
}
