package com.example;

import java.util.Objects;

public class Food {
    private int id;
    private int restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private boolean enabled;


    public Food() {
    }

    public Food(int id, int restaurantId, String restaurantName, String name, String description, double price, boolean enabled) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.enabled = enabled;
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

    public String getRestaurantName() { 
        return this.restaurantName; 
    }

    public void setRestaurantName(String restaurantName) { 
        this.restaurantName = restaurantName; 
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Food)) {
            return false;
        }
        Food food = (Food) o;
        return id == food.id && restaurantId == food.restaurantId && Objects.equals(name, food.name) && Objects.equals(description, food.description) && quantity == food.quantity && price == food.price && enabled == food.enabled;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", restaurantId='" + getRestaurantId() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", quantity='" + getQuantity() + "'" +
            ", price='" + getPrice() + "'" +
            ", enabled='" + isEnabled() + "'" +
            "}";
    }
    
}

