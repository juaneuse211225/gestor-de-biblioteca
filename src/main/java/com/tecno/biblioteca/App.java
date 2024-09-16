package com.tecno.biblioteca;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setHeight(480);
            primaryStage.setWidth(605);
            primaryStage.show();

        } catch (IOException e) {

            System.err.println("Se ha producido una exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        launch(args);

    }
}
