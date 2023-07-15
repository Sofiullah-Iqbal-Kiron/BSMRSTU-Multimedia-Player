package com.multimediaplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.mainStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EntireUI.fxml"));
        var theController = loader.getController();  // saving the controller for future

        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        init_stage(stage);
    }

    private void init_stage(Stage stage) {
        Scene scene = stage.getScene();
        scene.setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2) {
                Platform.runLater(() -> {
                    stage.setFullScreen(!stage.isFullScreen());
                });
            }
        });

        stage.setTitle("BSMRSTU Media Player");
        stage.toFront();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}