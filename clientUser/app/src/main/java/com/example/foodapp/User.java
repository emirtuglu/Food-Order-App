package com.example.foodapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String mail;
    private String password;
    private ArrayList<Address> addresses;
    private ArrayList<Order> orders;
    private ArrayList<Food> cart;  // Associate foods with their quantities


    public User() {
    }

    public User(int id, String name, String surname, String phoneNumber, String mail, ArrayList<Address> addresses, ArrayList<Order> orders, ArrayList<Food> cart) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.addresses = addresses;
        this.orders = orders;
        this.cart = cart;
    }

    public User(String name, String surname, String phoneNumber, String mail, String password) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.password = password;
    }

    public User(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public void changeQuantityOfFoodInCart (int id, int changeAmount) {
        for (Food food : cart) {
            if (food.getId() == id) {
                food.setQuantity(food.getQuantity() + changeAmount);
            }
        }
    }

    public double getTotalPriceOfCart () {
        double price = 0;
        for (Food food : cart) {
            price += food.getPrice() * food.getQuantity();
        }
        return price;
    }

    public Order getLastOrder() {
        if (orders == null || orders.isEmpty()) {
            return null;
        }

        // Sort orders based on time in descending order
        orders.sort(Comparator.comparing(Order::getTime, Comparator.reverseOrder()));

        return orders.get(0);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public ArrayList<Food> getCart() {
        return this.cart;
    }

    public void setCart(ArrayList<Food> cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(mail, user.mail) && Objects.equals(password, user.password) && Objects.equals(addresses, user.addresses) && Objects.equals(orders, user.orders) && Objects.equals(cart, user.cart);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                ", surname='" + getSurname() + "'" +
                ", phoneNumber='" + getPhoneNumber() + "'" +
                ", mail='" + getMail() + "'" +
                ", password='" + getPassword() + "'" +
                ", addresses='" + getAddresses() + "'" +
                ", orders='" + getOrders() + "'" +
                ", cart='" + getCart() + "'" +
                "}";
    }

}

