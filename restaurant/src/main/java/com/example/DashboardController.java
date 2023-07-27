package com.example;

import java.util.ArrayList;

import com.google.gson.Gson;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardController {
    @FXML
    private VBox outerBox;
    @FXML
    private ListView<Order> ordersListView;
    @FXML
    private ListView<Food> menuListView;
    @FXML
    private Button newFoodButton;
    @FXML
    private Label restaurantName;
    @FXML
    private ImageView restaurantImage;

    private static Restaurant restaurant;
    private ObservableList<Food> menuItems;
    private ObservableList<Order> orderItems;
    private Gson gson;

    
    public void initialize() {
        // Set restaurant image and name
        Image image = new Image("file:src\\main\\resources\\com\\example\\Images\\default_restaurant_logo.png");
        restaurantImage.setImage(image);
        restaurantImage.setFitHeight(70);
        restaurantImage.setFitWidth(70);
        restaurantName.setText(restaurant.getName());

        newFoodButton.prefWidthProperty().bind(menuListView.widthProperty());
        newFoodButton.setOnMouseClicked(event -> handleNewFoodButton());
        gson = new Gson();

        menuItems = FXCollections.observableArrayList(restaurant.getMenu());
        orderItems = FXCollections.observableArrayList(restaurant.getOrders());
         
        menuListView.setCellFactory(param -> new ListCell<Food>() {
            @Override
            protected void updateItem(Food item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null ) {
                    setText(null);
                } else {
                    HBox foodBox = createFoodBox(item);
                    setGraphic(foodBox);
                    ImageView editIconView = (ImageView) foodBox.lookup("#editIconView");
                    ImageView deleteIconView = (ImageView) foodBox.lookup("#deleteIconView");
                    editIconView.setOnMouseClicked(event -> handleEditFood(item));
                    deleteIconView.setOnMouseClicked(evenet -> handleDeleteFood(item));
                }
            }
        });
        menuListView.setItems(menuItems);

        ordersListView.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    HBox orderBox = createOrderBox(item);
                    setGraphic(orderBox);
                }
            }
        });
        ordersListView.setItems(orderItems);
    }

    private HBox createFoodBox (Food food) {
        HBox foodBox = new HBox();
        foodBox.setPrefWidth(500);
        foodBox.setSpacing(30);
        foodBox.setPadding(new Insets(10));

        Image foodImage = new Image("file:src\\main\\resources\\com\\example\\Images\\default_food_picture.png");
        ImageView foodImageView = new ImageView();
        foodImageView.setFitHeight(50);
        foodImageView.setFitWidth(50);
        foodImageView.setImage(foodImage);
        
        VBox foodNameAndDescription = new VBox();
        foodNameAndDescription.setPrefWidth(200);

        Label foodName = new Label(food.getName());
        foodName.setFont(new Font("Arial", 20));
        Label foodDescription = new Label(food.getDescription());
        foodDescription.setFont(new Font("Arial", 12));
        foodDescription.setWrapText(true);
        foodNameAndDescription.getChildren().addAll(foodName, foodDescription);

        StackPane foodPricePane = new StackPane();
        foodPricePane.setAlignment(Pos.CENTER);
        Label foodPrice = new Label(Double.toString(food.getPrice()));
        foodPrice.setFont(new Font("Arial", 15));
        foodPricePane.getChildren().add(foodPrice);

        VBox enableBox = new VBox();
        enableBox.setAlignment(Pos.CENTER);
        Label enableText = new Label("Active");
        enableText.setFont(new Font("Arial", 12));
        SwitchButton foodEnabled = new SwitchButton();
        foodEnabled.setPrefHeight(30);
        foodEnabled.setPrefWidth(50);
        foodEnabled.setState(food.isEnabled());
        enableBox.getChildren().addAll(enableText, foodEnabled);

        VBox iconsBox = new VBox();
        iconsBox.setSpacing(10);
        Image editIcon = new Image("file:src\\main\\resources\\com\\example\\Images\\edit_icon.png");
        ImageView editIconView = new ImageView();
        editIconView.setFitHeight(20);
        editIconView.setFitWidth(20);
        editIconView.setImage(editIcon);
        editIconView.setId("editIconView");
        Image deleteIcon = new Image("file:src\\main\\resources\\com\\example\\Images\\delete_icon.png");
        ImageView deleteIconView = new ImageView();
        deleteIconView.setFitHeight(20);
        deleteIconView.setFitWidth(20);
        deleteIconView.setImage(deleteIcon);
        deleteIconView.setId("deleteIconView");
        iconsBox.getChildren().addAll(editIconView, deleteIconView);

        foodBox.getChildren().addAll(foodImageView, foodNameAndDescription, foodPricePane, enableBox, iconsBox);
        return foodBox;
    }

    private HBox createOrderBox(Order order) {
        HBox orderBox = new HBox();
        orderBox.setPrefWidth(500);
        orderBox.setSpacing(20);
        orderBox.setPadding(new Insets(10));

        VBox userBox = new VBox();
        userBox.setPrefWidth(200);
        userBox.setSpacing(3);
        Label userFullName = new Label(order.getUser().getFullName());
        userFullName.setFont(new Font("Arial", 16));
        Label userPhoneNumber = new Label(order.getUser().getPhoneNumber());
        userPhoneNumber.setFont(new Font("Arial", 14));
        Label userCityAndDistrict = new Label(order.getUser().getAddresses().get(0).getCity() + " / " + order.getUser().getAddresses().get(0).getDistrict());
        userCityAndDistrict.setFont(new Font("Arial", 14));
        Label userFullAddress = new Label(order.getUser().getAddresses().get(0).getFullAddress());
        userFullAddress.setFont(new Font("Arial", 14));
        userFullAddress.setWrapText(true);
        userBox.getChildren().addAll(userFullName, userPhoneNumber, userCityAndDistrict, userFullAddress);


        ObservableList<Food> orderedFoods = FXCollections.observableArrayList(order.getFoods());
        ListView<Food> foodsView = new ListView<>(orderedFoods);
        foodsView.setCellFactory(param -> new ListCell<Food>() {
            @Override
            protected void updateItem(Food item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    HBox orderedFoodsBox = getOrderedFoodsBox(item);
                    setGraphic(orderedFoodsBox);
                }
            }
        });
        foodsView.setPrefHeight(100);

        VBox priceBox = new VBox();
        priceBox.setPrefWidth(100);
        priceBox.setSpacing(20);
        Label statusLabel = new Label(order.getStatusExplanation());
        if (order.getStatus() == Status.USER_REQUESTED_CANCEL) {
            statusLabel.setTextFill(Color.RED);
        }
        else if (order.getStatus() == Status.COMPLETED) {
            statusLabel.setTextFill(Color.GREEN);
        }
        statusLabel.setFont(new Font("Arial", 16));
        Label totalPrice = new Label("₺" + Double.toString(order.getPrice()));
        totalPrice.setFont(new Font("Arial", 16));
        priceBox.getChildren().addAll(statusLabel, totalPrice);

        VBox buttonBox = new VBox();
        buttonBox.setPrefWidth(150);
        buttonBox.setSpacing(20);
        Label dateLabel = new Label(order.getTime());
        dateLabel.setFont(new Font("Arial", 14));
        Button cancelButton = new Button();
        cancelButton.setText("Cancel");
        cancelButton.setPrefWidth(60);
        cancelButton.setVisible(order.getStatus() == Status.ACTIVE || order.getStatus() == Status.USER_REQUESTED_CANCEL);
        cancelButton.setOnMouseClicked(event -> handleCancelOrder(order));
        buttonBox.getChildren().addAll(dateLabel, cancelButton);

        orderBox.getChildren().addAll(userBox, foodsView, priceBox, buttonBox);
        return orderBox;
    }

    private HBox getOrderedFoodsBox(Food food) {
        HBox orderedFoodsBox = new HBox();
        orderedFoodsBox.setPrefWidth(200);
        orderedFoodsBox.setSpacing(3);
        Label foodQuantityAndName = new Label("x" + Integer.toString(food.getQuantity()) + " " + food.getName());
        foodQuantityAndName.setFont(new Font("Arial", 14));
        orderedFoodsBox.getChildren().addAll(foodQuantityAndName);
        return orderedFoodsBox;
    }

    private void handleEditFood(Food food) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Food");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(food.getName());
        TextArea descriptionField = new TextArea(food.getDescription());
        TextField priceField = new TextField("₺" + Double.toString(food.getPrice()));
        descriptionField.setPrefRowCount(3);

        grid.add(new Label("Food Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            food.setName(nameField.getText());
            food.setDescription(descriptionField.getText());
            try {
                Double.parseDouble(priceField.getText());
                food.setPrice(Double.valueOf(priceField.getText()));
            } catch (Exception e) {

            }
        }
        return null;
        });

        dialog.showAndWait();
    }

    private void handleDeleteFood(Food food) {

    }


    private void handleCancelOrder(Order order) {

    }

    private void handleNewFoodButton() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Food");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        descriptionField.setPrefRowCount(3);

        grid.add(new Label("Food Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            try {
                Food food = new Food();
                food.setName(nameField.getText());
                food.setDescription(descriptionField.getText());
                Double.parseDouble(priceField.getText());
                food.setPrice(Double.valueOf(priceField.getText()));
                food.setRestaurantId(restaurant.getId());
                food.setEnabled(true);

                String foodJson = gson.toJson(food, Food.class);
                String request = RequestManager.requestBuild("POST", "/add-food", null, null, foodJson);
                String response = RequestManager.sendRequest(request);
                if (response.contains("201 Created")) {
                    //restaurant.getMenu().add(food);
                    menuItems.add(food);
                }
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Food couldn't be added");
                    alert.setContentText(RequestManager.getBody(response));
                    alert.showAndWait();
                }
                
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Food couldn't be added. Please enter valid inputs.");
                alert.showAndWait();
            }
        }
        return null;
        });

        dialog.showAndWait();
    }

    public static void setRestaurant(Restaurant aRestaurant) {
        restaurant = aRestaurant;
    }

}
