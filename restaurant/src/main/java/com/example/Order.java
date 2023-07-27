package com.example;

import java.util.Objects;
import java.util.ArrayList;

enum Status {
    ACTIVE, COMPLETED, USER_REQUESTED_CANCEL, USER_CANCELLED, RESTAURANT_CANCELLED
}

public class Order {
    private int id;
    private User user;
    private String time;
    private double price;
    private Status status;
    private ArrayList<Food> foods;

    public Order() {
    }

    public String getStatusExplanation() {
        if (this.status == Status.ACTIVE) {
            return "Active";
        }
        else if (this.status == Status.COMPLETED) {
            return "Completed";
        }
        else if (this.status == Status.RESTAURANT_CANCELLED) {
            return "Cancelled by you";
        }
        else if (this.status == Status.USER_CANCELLED) {
            return "Cancelled by user";
        }
        else if (this.status == Status.USER_REQUESTED_CANCEL) {
            return "User requested cancellation";
        }
        return "";
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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
}


