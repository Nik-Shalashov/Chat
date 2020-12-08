package client;

import client.controllers.AuthController;
import client.controllers.ChatController;
import client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NetworkClient extends Application {


    public final String PATH_TO_FXML = "views/sample.fxml";
    private Stage primaryStage;
    private Stage authStage;
    private Network network;
    private ChatController chatController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        network = new Network();
        if (!network.connect()) {
            showErrorMsg("Problems with connection", "", "Unable to connect to server!");
            return;
        }

        openAuthWindow();
        createMainChatWindow();
    }

    private void openAuthWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NetworkClient.class.getResource("views/auth-view.fxml"));
        Parent root = loader.load();
        authStage = new Stage();

        authStage.setTitle("Authorization");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        authStage.setScene(scene);
        authStage.show();

        AuthController authController = loader.getController();
        authController.setNetwork(network);
        authController.setNetworkClient(this);

    }

    public void createMainChatWindow() throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NetworkClient.class.getResource(PATH_TO_FXML));

        Parent root = loader.load();

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root));

        chatController = loader.getController();
        chatController.setNetwork(network);

        primaryStage.setOnCloseRequest(windowEvent -> network.close());
    }

    public static void showErrorMsg(String title, String msg, String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(msg);
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openMainChatWindow() {
        authStage.close();
        primaryStage.show();

        primaryStage.setTitle(network.getUsername());
        chatController.setLabel(network.getUsername());
        network.waitMessage(chatController);
    }
}
