package org.tecno.gestor.biblioteca;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static Thread thread;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setHeight(480);
            primaryStage.setWidth(605);
            Image a = new Image(getClass().getResource("/img/image832.png").toString());
            primaryStage.getIcons().add(a);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
                Platform.exit();
            });

        } catch (IOException e) {

            System.err.println("Se ha producido una exception: " + e.getMessage());
        }
    }

    @Override
    public void init() throws Exception {
        HiloVerificacionPrestamos hilo = new HiloVerificacionPrestamos();
        thread = new Thread(hilo);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) throws Exception {

        launch(args);

    }
}
