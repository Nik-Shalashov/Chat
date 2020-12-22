package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends Application {


    public final String PATH_TO_FXML = "sample.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource(PATH_TO_FXML));
        Parent root = loader.load();

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Network network = new Network();
        if (!network.connect()) {
            showErrorMsg("Problems with connection", "", "Unable to connect to server!");
        }

        ViewController viewController = loader.getController();
        viewController.setNetwork(network);

        network.waitMessage(viewController);

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
}
