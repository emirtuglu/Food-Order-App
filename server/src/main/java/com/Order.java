package com;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.HashMap;

enum Status {
    ACTIVE, COMPLETED, CANCEL_REQUESTED, CANCELLED
}

public class Order {
    private int id;
    private Restaurant restaurant;
    private User user;
    private LocalDateTime time;
    private double price;
    private Status status;
    private HashMap<Food, Integer> foods;

    public Order() {
    }

    public Order(int id, Restaurant restaurant, User user, LocalDateTime time, double price, Status status) {
        this.id = id;
        this.restaurant = restaurant;
        this.user = user;
        this.time = time;
        this.price = price;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public HashMap<Food, Integer> getFoods() {
        return this.foods;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Order)) {
            return false;
        }
        Order order = (Order) o;
        return id == order.id && Objects.equals(restaurant, order.restaurant) && Objects.equals(user, order.user) && Objects.equals(time, order.time) && price == order.price && Objects.equals(status, order.status);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", restaurant='" + getRestaurant() + "'" +
            ", user='" + getUser() + "'" +
            ", time='" + getTime() + "'" +
            ", price='" + getPrice() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}


