package com.example;

import java.util.ArrayList;
import java.util.Objects;

public class Restaurant {
    private int id;
    private Address address;
    private String name;
    private String phoneNumber;
    private String mail;
    private String password;
    private ArrayList<Food> menu;
    private ArrayList<Order> orders;
    private byte[] image;

    public Restaurant() {
    }

    public Restaurant (String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public Restaurant(Address address, String name, String phoneNumber, String mail, String password) {
        this.address = address;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.password = password;
    }

    public ArrayList<Order> getOrdersInStatus (Status status) {
        ArrayList<Order> result = new ArrayList<Order>();
        for (Order order : this.orders) {
            if (order.getStatus() == status) {
                result.add(order);
            }
        }
        return result;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<Food> getMenu() {
        return this.menu;
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public ArrayList<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Restaurant)) {
            return false;
        }
        Restaurant restaurant = (Restaurant) o;
        return id == restaurant.id && Objects.equals(address, restaurant.address) && Objects.equals(name, restaurant.name) && Objects.equals(phoneNumber, restaurant.phoneNumber) && Objects.equals(mail, restaurant.mail) && Objects.equals(password, restaurant.password) && Objects.equals(menu, restaurant.menu) && Objects.equals(orders, restaurant.orders);
    }
}

