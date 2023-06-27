package com.multimediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EntireUI.fxml"));
        var theController = loader.getController();  // saving the controller for future

        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("BSMRSTU Media Player");
        stage.toFront();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}