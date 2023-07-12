package com;
import java.util.Objects;

public class Food {
    private int id;
    private Restaurant restaurant;
    private String name;
    private double price;
    private boolean enabled;

    public Food() {
    }

    public Food(int id, Restaurant restaurant, String name, double price, boolean enabled) {
        this.id = id;
        this.restaurant = restaurant;
        this.name = name;
        this.price = price;
        this.enabled = enabled;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
        return id == food.id && Objects.equals(restaurant, food.restaurant) && Objects.equals(name, food.name) && Objects.equals(price, food.price) && enabled == food.enabled;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", restaurant='" + getRestaurant() + "'" +
            ", name='" + getName() + "'" +
            ", price='" + getPrice() + "'" +
            ", enabled='" + isEnabled() + "'" +
            "}";
    }
}

