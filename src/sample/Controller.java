package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField inputField;

    @FXML
    private ListView<String> listView;

    private final ObservableList<String> wordList = FXCollections.observableArrayList("Вы присоединились к чату!");

    @FXML
    public void initialize() {
        listView.setItems(wordList);
    }

    @FXML
    public void addWord() {
        String word = inputField.getText();
        if (word.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Empty message");
            alert.setContentText("Message successfully was not sent :)");
            alert.showAndWait();
            return;
        } else {
            addWordToList(word);
        }
        inputField.clear();
    }


    private void addWordToList(String word) {
        listView.getItems().add(word);
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
