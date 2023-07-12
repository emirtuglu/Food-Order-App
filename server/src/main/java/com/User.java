package com;
import java.util.ArrayList;
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
    private HashMap<Food, Integer> cart;  // Associate foods with their quantities

    public User() {
    }

    public User(int id, String name, String surname, String phoneNumber, String mail, ArrayList<Address> addresses, HashMap<Food,Integer> cart) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.addresses = addresses;
        this.cart = cart;
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

    public HashMap<Food,Integer> getCart() {
        return this.cart;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(mail, user.mail) && Objects.equals(addresses, user.addresses) && Objects.equals(cart, user.cart);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", mail='" + getMail() + "'" +
            ", addresses='" + getAddresses() + "'" +
            ", cart='" + getCart() + "'" +
            "}";
    }
}
