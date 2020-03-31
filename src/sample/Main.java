package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        primaryStage.setTitle("Contact list");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.setOnCloseRequest(event -> mainController.checkIsSavedAndClose());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
