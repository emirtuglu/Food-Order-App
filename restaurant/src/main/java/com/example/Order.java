package com.example;

import java.util.Objects;
import java.util.ArrayList;

enum Status {
    ACTIVE, COMPLETED, USER_REQUESTED_CANCEL, USER_CANCELLED, RESTAURANT_CANCELLED
}

public class Order {
    private int id;
    private int restaurantId;
    private int userId;
    private String restaurantName;
    private String time;
    private double price;
    private Status status;
    private ArrayList<Food> foods;

    public Order() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }

    public void setRestaurantName( String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<Food> getFoods() {
        return this.foods;
    }

    public void setFoods(ArrayList<Food> foods) {
        this.foods = foods;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Order)) {
            return false;
        }
        Order order = (Order) o;
        return id == order.id && restaurantId == order.restaurantId && userId == order.userId && Objects.equals(time, order.time) && price == order.price && Objects.equals(status, order.status) && Objects.equals(foods, order.foods);
    }
}


