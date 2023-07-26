package com.example;

import java.io.IOException;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField restaurantNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField districtField;
    @FXML
    private TextArea fullAddressField;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void register() throws IOException {
        Gson gson = new Gson();
        String restaurantName = restaurantNameField.getText().strip();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phoneNumber = phoneNumberField.getText();
        String city = cityField.getText().strip();
        String district = districtField.getText().strip();
        String fullAddress = fullAddressField.getText();

        Address address = new Address(city, district, fullAddress);
        Restaurant restaurant = new Restaurant(address, restaurantName, phoneNumber, email, password);
        String restaurantJson = gson.toJson(restaurant, Restaurant.class);
        String request = RequestManager.requestBuild("POST", "/restaurant-register", null, null, restaurantJson);
        String response = RequestManager.sendRequest(request);

        if (response.contains("201 Created")) {
            restaurantNameField.clear();
            emailField.clear();
            passwordField.clear();
            phoneNumberField.clear();
            cityField.clear();
            districtField.clear();
            fullAddressField.clear();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Your registration was successful!");

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        App.setRoot("primary");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            });
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setHeaderText(null);
            alert.setContentText(RequestManager.getBody(response));
            alert.showAndWait();
        }
    }
    
}
