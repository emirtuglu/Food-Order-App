package com;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.HashMap;

enum Status {
    ACTIVE, COMPLETED, USER_REQUESTED_CANCEL, USER_CANCELLED, RESTAURANT_CANCELLED
}

public class Order {
    private int id;
    private int restaurantId;
    private int userId;
    private LocalDateTime time;
    private double price;
    private Status status;
    private HashMap<Food, Integer> foods;

    public Order() {
    }

    public Order(int id, int restaurantId, int userId, LocalDateTime time, double price, Status status, HashMap<Food,Integer> foods) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.time = time;
        this.price = price;
        this.status = status;
        this.foods = foods;
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

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
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

    public HashMap<Food,Integer> getFoods() {
        return this.foods;
    }

    public void setFoods(HashMap<Food,Integer> foods) {
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

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", restaurantId='" + getRestaurantId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", time='" + getTime() + "'" +
            ", price='" + getPrice() + "'" +
            ", status='" + getStatus() + "'" +
            ", foods='" + getFoods() + "'" +
            "}";
    }
}


