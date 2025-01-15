package com.example.breakitdown;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BreakItDownApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image iconImage = new Image(Objects.requireNonNull(BreakItDownApplication.class.getResourceAsStream("/Images/breakItDownIcon.png")));

        FXMLLoader fxmlLoader = new FXMLLoader(BreakItDownApplication.class.getResource("playlist-submission.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Break-It-Down");
        stage.getIcons().add(iconImage);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}