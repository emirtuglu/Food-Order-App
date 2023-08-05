package com.example;

import java.util.Objects;
import java.util.ArrayList;

enum Status {
    PENDING_APPROVAL, PREPARING, COMPLETED, USER_REQUESTED_CANCEL, USER_CANCELLED, RESTAURANT_CANCELLED
}

public class Order {
    private int id;
    private int userId;
    private int restaurantId;
    private String restaurantName;
    private Address userAddress;
    private String userFullName;
    private String userPhoneNumber;
    private String time;
    private double price;
    private Status status;
    private ArrayList<Food> foods;


    public Order() {
    }

    public Order(int id, int userId, int restaurantId, String restaurantName, Address userAddress, String userFullName, String userPhoneNumber, String time, double price, Status status, ArrayList<Food> foods) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.userAddress = userAddress;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.time = time;
        this.price = price;
        this.status = status;
        this.foods = foods;
    }

    public String getStatusExplanation() {
        if (this.status == Status.PENDING_APPROVAL) {
            return "Pending your approval";
        }
        else if (this.status == Status.PREPARING) {
            return "Preparing";
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

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }

    public void setRestaurantName (String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Address getUserAddress() {
        return this.userAddress;
    }

    public void setUserAddress (Address userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserFullName() {
        return this.userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhoneNumber() {
        return this.userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
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
        return id == order.id && userId == order.userId && restaurantId == order.restaurantId && userAddress == order.userAddress && Objects.equals(userFullName, order.userFullName) && Objects.equals(userPhoneNumber, order.userPhoneNumber) && Objects.equals(time, order.time) && price == order.price && Objects.equals(status, order.status) && Objects.equals(foods, order.foods);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", restaurantId='" + getRestaurantId() + "'" +
            ", userAddressId='" + getUserAddress() + "'" +
            ", userFullName='" + getUserFullName() + "'" +
            ", userPhoneNumber='" + getUserPhoneNumber() + "'" +
            ", time='" + getTime() + "'" +
            ", price='" + getPrice() + "'" +
            ", status='" + getStatus() + "'" +
            ", foods='" + getFoods() + "'" +
            "}";
    }
    
}


