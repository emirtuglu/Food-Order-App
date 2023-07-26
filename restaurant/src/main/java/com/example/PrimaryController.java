package com.example;

import java.io.IOException;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.google.gson.Gson;

public class PrimaryController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void login() throws IOException {

        Gson gson = new Gson();

        String email = emailField.getText();
        String password = passwordField.getText();
        Restaurant restaurant = new Restaurant(email, password);
        String restaurantJson = gson.toJson(restaurant, Restaurant.class);
        String request = RequestManager.requestBuild("POST", "/restaurant-login", null, null, restaurantJson);
        String response = RequestManager.sendRequest(request);
        
        if (response.contains("200 OK")) {
            try {
                App.startDashboard();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText(RequestManager.getBody(response));
            alert.showAndWait();
        }
    }
}
