package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

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
    @FXML
    private RadioButton allOrdersButton;

    @FXML
    private RadioButton activeOrdersButton;

    @FXML
    private RadioButton pastOrdersButton;

    private ToggleGroup toggleGroup;
    private static Restaurant restaurant;
    private static ObservableList<Food> menuItems;
    private static ObservableList<Order> orderItems;
    private static Gson gson;

    
    public void initialize() {
        // Set restaurant image and name
        Image image;
        if (restaurant.getImage() == null) {
            image = new Image("file:src\\main\\resources\\com\\example\\Images\\default_restaurant_logo.png");
        }
        else {
            image = new Image(new ByteArrayInputStream(restaurant.getImage()));
        }
        restaurantImage.setImage(image);
        restaurantImage.setFitHeight(70);
        restaurantImage.setFitWidth(70);
        restaurantImage.setOnMouseClicked(e -> handleEditRestaurantImage());
        restaurantName.setText(restaurant.getName());


        if (restaurant.getOrders() == null) {
            restaurant.setOrders(new ArrayList<Order>());
        }

        newFoodButton.prefWidthProperty().bind(menuListView.widthProperty());
        newFoodButton.setOnMouseClicked(event -> handleNewFoodButton());
        gson = new Gson();

        toggleGroup = new ToggleGroup();
        allOrdersButton.setToggleGroup(toggleGroup);
        activeOrdersButton.setToggleGroup(toggleGroup);
        pastOrdersButton.setToggleGroup(toggleGroup);
        allOrdersButton.setSelected(true);
        allOrdersButton.setOnMouseClicked(event -> handleRadioButtons(allOrdersButton));
        activeOrdersButton.setOnMouseClicked(event -> handleRadioButtons(activeOrdersButton));
        pastOrdersButton.setOnMouseClicked(event -> handleRadioButtons(pastOrdersButton));

        menuItems = FXCollections.observableArrayList(restaurant.getMenu());
        orderItems = FXCollections.observableArrayList(restaurant.getOrders());
        
        orderItems.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
        menuItems.sort(Comparator.comparing(Food::getName));
         
        menuListView.setCellFactory(param -> new ListCell<Food>() {
            @Override
            protected void updateItem(Food item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null ) {
                    setGraphic(null);
                } else {
                    HBox foodBox = createFoodBox(item);
                    if (item.isEnabled()) {
                        foodBox.setOpacity(1);
                        foodBox.setStyle("-fx-background-color: white;");
                    }
                    else {
                        foodBox.setOpacity(0.8);
                        foodBox.setStyle("-fx-background-color: gray;");
                    }
                    setGraphic(foodBox);
                    ImageView editIconView = (ImageView) foodBox.lookup("#editIconView");
                    ImageView deleteIconView = (ImageView) foodBox.lookup("#deleteIconView");
                    editIconView.setOnMouseClicked(event -> handleEditFood(item));
                    deleteIconView.setOnMouseClicked(event -> handleDeleteFood(item));
                }
            }
        });
        menuListView.setItems(menuItems);

        ordersListView.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox orderBox = createOrderBox(item);
                    setGraphic(orderBox);
                }
            }
        });
        ordersListView.setItems(orderItems);

        // Task to update status of orders in every 10 seconds
        updateOrders();

    }

    private HBox createFoodBox (Food food) {
        HBox foodBox = new HBox();
        foodBox.setPrefWidth(500);
        foodBox.setSpacing(25);
        foodBox.setPadding(new Insets(10));

        Image foodImage;
        if (food.getImage() == null) {
            foodImage = new Image("file:src\\main\\resources\\com\\example\\Images\\default_food_picture.png");
        }
        else {
            foodImage = new Image(new ByteArrayInputStream(food.getImage()));
        }
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
        foodPricePane.setPrefWidth(100);
        foodPricePane.setAlignment(Pos.CENTER);
        Label foodPrice = new Label("₺" + Double.toString(food.getPrice()));
        foodPrice.setFont(new Font("Arial", 15));
        foodPricePane.getChildren().add(foodPrice);

        VBox enableBox = new VBox();
        enableBox.setSpacing(3);
        enableBox.setAlignment(Pos.CENTER);
        Label enableText = new Label(food.isEnabled() ? "Listed" : "Unlisted");
        enableText.setTextFill(food.isEnabled() ? Color.GREEN : Color.RED);
        enableText.setFont(new Font("Arial", 12));
        Button foodEnabled = new Button();
        foodEnabled.setPrefHeight(30);
        foodEnabled.setPrefWidth(75);
        foodEnabled.setText(food.isEnabled() ? "Unlist" : "List");
        foodEnabled.setOnMouseClicked(event -> handleEnabledButton(food, enableText));
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
        Label userFullName = new Label(order.getUserFullName());
        userFullName.setFont(new Font("Arial", 16));
        Label userPhoneNumber = new Label(order.getUserPhoneNumber());
        userPhoneNumber.setFont(new Font("Arial", 14));
        Label userCityAndDistrict = new Label(order.getUserAddress().getCity() + " / " + order.getUserAddress().getDistrict());
        userCityAndDistrict.setFont(new Font("Arial", 14));
        Label userFullAddress = new Label(order.getUserAddress().getFullAddress());
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
        priceBox.setPrefWidth(150);
        priceBox.setSpacing(20);
        Label statusLabel = new Label(order.getStatusExplanation());
        if (order.getStatus() == Status.PREPARING) { 
            statusLabel.setTextFill(Color.BLUE);
        }
        else if (order.getStatus() == Status.PENDING_APPROVAL) {
            statusLabel.setTextFill(Color.valueOf("AABF23"));
        }
        else if (order.getStatus() == Status.USER_REQUESTED_CANCEL) {
            statusLabel.setTextFill(Color.PURPLE);
        }
        else if (order.getStatus() == Status.COMPLETED) {
            statusLabel.setTextFill(Color.GREEN);
        }
        else if (order.getStatus() == Status.USER_CANCELLED || order.getStatus() == Status.RESTAURANT_CANCELLED) {
            statusLabel.setTextFill(Color.RED);
        }
        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setWrapText(true);
        Label totalPrice = new Label("₺" + String.format("%.2f",order.getPrice()));
        totalPrice.setFont(new Font("Arial", 16));
        priceBox.getChildren().addAll(statusLabel, totalPrice);

        VBox buttonBox = new VBox();
        buttonBox.setPrefWidth(150);
        buttonBox.setSpacing(18);
        Label dateLabel = new Label(order.getTime());
        dateLabel.setFont(new Font("Arial", 14));
        Button completeButton = new Button();
        completeButton.setText(order.getStatus() == Status.PREPARING ? "Complete" : "Approve");
        completeButton.setPrefWidth(75);
        completeButton.setVisible(order.getStatus() == Status.PREPARING || order.getStatus() == Status.PENDING_APPROVAL);
        completeButton.setOnMouseClicked(event -> handleCompleteOrder(order));
        Button cancelButton = new Button();
        cancelButton.setText("Cancel");
        cancelButton.setPrefWidth(75);
        cancelButton.setVisible(order.getStatus() == Status.PENDING_APPROVAL || order.getStatus() == Status.PREPARING || order.getStatus() == Status.USER_REQUESTED_CANCEL);
        cancelButton.setOnMouseClicked(event -> handleCancelOrder(order));
        buttonBox.getChildren().addAll(dateLabel, completeButton, cancelButton);

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
        TextField priceField = new TextField(Double.toString(food.getPrice()));
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);

        GridPane imageGrid = new GridPane();
        Label imageLabel = new Label("");
        imageLabel.setVisible(false);
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");

            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Image Files", "*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(imageFilter);

            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    RandomAccessFile f = new RandomAccessFile(file.getAbsolutePath(), "r");
                    byte[] bytes = new byte[(int) f.length()];
                    f.read(bytes);
                    f.close();
                    food.setImage(bytes);
                    imageLabel.setVisible(true);
                    imageLabel.setText(file.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        imageGrid.add(chooseImageButton, 0, 0);
        imageGrid.add(imageLabel, 2, 0);
        imageGrid.setHgap(10);

        grid.add(new Label("Food Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(imageGrid, 0, 4, 2, 1);

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
            menuItems.sort(Comparator.comparing(Food::getName));
            menuListView.refresh();
            String foodJson = gson.toJson(food, Food.class);
            String request = RequestManager.requestBuild("POST", "/update-food", null, null, foodJson);
            RequestManager.sendRequest(request);
        }
        return null;
        });

        dialog.showAndWait();
    }

    private void handleDeleteFood(Food food) {
        // Ask user to confirm deletion
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this food?");

        ButtonType confirmButton = new ButtonType("Yes", ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No", ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        // Delete food if user clicks yes
        if (result == confirmButton) {
            menuItems.remove(food);
            String foodJson = gson.toJson(food, Food.class);
            String request = RequestManager.requestBuild("POST", "/delete-food", null, null, foodJson);
            RequestManager.sendRequest(request);
            menuListView.refresh();
        }
    }

    private void handleCompleteOrder(Order order) {
        if (order.getStatus() == Status.PENDING_APPROVAL) {
            order.setStatus(Status.PREPARING);
        }
        else {
            order.setStatus(Status.COMPLETED);
        }
        String orderJson = gson.toJson(order, Order.class);
        String request = RequestManager.requestBuild("POST", "/update-order", null, null, orderJson);
        RequestManager.sendRequest(request);
        ordersListView.refresh();
    }


    private void handleCancelOrder(Order order) {
        // Ask user to confirm cancellation
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Cancellation");
        confirmationAlert.setHeaderText("Are you sure you want to cancel this order?");

        ButtonType confirmButton = new ButtonType("Yes", ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No", ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        // If user clicks yes, cancel the order
        if (result == confirmButton) {
            if (order.getStatus() == Status.USER_REQUESTED_CANCEL) {
                order.setStatus(Status.USER_CANCELLED);
            }
            else {
                order.setStatus(Status.RESTAURANT_CANCELLED);
            }
            String orderJson = gson.toJson(order, Order.class);
            String request = RequestManager.requestBuild("POST", "/update-order", null, null, orderJson);
            RequestManager.sendRequest(request);
            ordersListView.refresh();
        }
    }

    private void handleNewFoodButton() {
        Food food = new Food();
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

        GridPane imageGrid = new GridPane();
        Label imageLabel = new Label("");
        imageLabel.setVisible(false);
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");

            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Image Files", "*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(imageFilter);

            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    RandomAccessFile f = new RandomAccessFile(file.getAbsolutePath(), "r");
                    byte[] bytes = new byte[(int) f.length()];
                    f.read(bytes);
                    f.close();
                    food.setImage(bytes);
                    imageLabel.setVisible(true);
                    imageLabel.setText(file.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        imageGrid.add(chooseImageButton, 0, 0);
        imageGrid.add(imageLabel, 2, 0);
        imageGrid.setHgap(10);

        grid.add(new Label("Food Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(imageGrid, 0, 4, 2, 1);
        
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            try {
                food.setName(nameField.getText());
                food.setDescription(descriptionField.getText());
                Double.parseDouble(priceField.getText());
                food.setPrice(Double.valueOf(priceField.getText()));
                food.setRestaurantId(restaurant.getId());
                food.setEnabled(true);
                food.setRestaurantName(restaurant.getName());

                String foodJson = gson.toJson(food, Food.class);
                String request = RequestManager.requestBuild("POST", "/add-food", null, null, foodJson);
                String response = RequestManager.sendRequest(request);
                if (response.contains("201 Created")) {
                    restaurant.getMenu().add(food);
                    menuItems.add(food);
                    menuItems.sort(Comparator.comparing(Food::getName));
                    menuListView.refresh();
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

    public void handleEnabledButton (Food food, Label enableTextLabel) {
        if (food.isEnabled()) {
            food.setEnabled(false);
            enableTextLabel.setText("Unlisted");
            enableTextLabel.setTextFill(Color.RED);
        }
        else {
            food.setEnabled(true);
            enableTextLabel.setText("Listed");
            enableTextLabel.setTextFill(Color.GREEN);
        }
        menuListView.refresh();
        String foodJson = gson.toJson(food, Food.class);
        String request = RequestManager.requestBuild("POST", "/update-food", null, null, foodJson);
        RequestManager.sendRequest(request);
    }

    public void updateOrders() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(() -> {
                String request = RequestManager.requestBuild("GET", "/restaurant-orders", "restaurantId", Integer.toString(restaurant.getId()), null);
                String response = RequestManager.sendRequest(request);
                String body = RequestManager.getBody(response);
                ArrayList<Order> updatedOrders = gson.fromJson(body, new TypeToken<List<Order>>(){}.getType());

                alertIfNewOrderExist(restaurant.getOrders(), updatedOrders);
                restaurant.getOrders().clear();
                restaurant.getOrders().addAll(updatedOrders);
                orderItems.clear();
                orderItems.addAll(updatedOrders);
                orderItems.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
            });
        }
        }, 1000, 10000);
    }

    public void alertIfNewOrderExist(ArrayList<Order> oldOrders, ArrayList<Order> newOrders) {
        // Check if there is new orders
        if (newOrders.size() > oldOrders.size()) {
            Media sound = new Media(getClass().getResource("Sounds/notification.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("New Order");
            alert.setHeaderText(null);
            alert.setContentText("You have a new order");
            alert.show();
        }
        // Check if there is any order that user requested cancel
        else if (oldOrders.size() == newOrders.size()) {
            oldOrders.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
            newOrders.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
            for (int i = 0; i < newOrders.size(); i++) {
                if (newOrders.get(i).getStatus() == Status.USER_REQUESTED_CANCEL && oldOrders.get(i).getStatus() != Status.USER_REQUESTED_CANCEL) {
                    Media sound = new Media(getClass().getResource("Sounds/notification.mp3").toExternalForm());
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.play();

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Order Cancel Request");
                    alert.setHeaderText(null);
                    alert.setContentText(newOrders.get(i).getUserFullName() + " wants to cancel the order");
                    alert.show();
                }
            }
        }
    }

    public void handleRadioButtons(RadioButton radioButton) {
        orderItems.clear();
        if (radioButton == allOrdersButton) {
            orderItems.addAll(restaurant.getOrders());

        } else if (radioButton == activeOrdersButton) {
            orderItems.addAll(restaurant.getOrdersInStatus(Status.PENDING_APPROVAL));
            orderItems.addAll(restaurant.getOrdersInStatus(Status.PREPARING));
            orderItems.addAll(restaurant.getOrdersInStatus(Status.USER_REQUESTED_CANCEL));
        } else if (radioButton == pastOrdersButton) {
            orderItems.addAll(restaurant.getOrdersInStatus(Status.COMPLETED));
            orderItems.addAll(restaurant.getOrdersInStatus(Status.RESTAURANT_CANCELLED));
            orderItems.addAll(restaurant.getOrdersInStatus(Status.USER_CANCELLED));
        }
        orderItems.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));
        ordersListView.refresh();
    }

    public static void setRestaurant(Restaurant aRestaurant) {
        restaurant = aRestaurant;
    }

    private void handleEditRestaurantImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
        "Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);

        Label imageLabel = new Label("");
        imageLabel.setVisible(false);
        File file = fileChooser.showOpenDialog(outerBox.getScene().getWindow());
        if (file != null) {
            try {
                RandomAccessFile f = new RandomAccessFile(file.getAbsolutePath(), "r");
                byte[] bytes = new byte[(int) f.length()];
                f.read(bytes);
                f.close();
                restaurantImage.setImage(new Image(new ByteArrayInputStream(bytes)));
                restaurant.setImage(bytes);
                imageLabel.setVisible(true);
                imageLabel.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        String restaurantJson = gson.toJson(restaurant, Restaurant.class);
        String request = RequestManager.requestBuild("POST", "/update-restaurant-image", null, null, restaurantJson);
        RequestManager.sendRequest(request);
    }

}
