package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 700, 550);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    static void startDashboard() throws IOException {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();

        Parent dashboard = loadFXML("dashboard");
        Scene dashBoardScene = new Scene(dashboard, bounds.getWidth(), bounds.getHeight());
        dashBoardScene.setRoot(dashboard);
        Stage stage = (Stage) scene.getWindow();
        stage.setScene(dashBoardScene);
        stage.setX(0);
        stage.setY(0);
    }

    public static void main(String[] args) {
        launch();
    }

}