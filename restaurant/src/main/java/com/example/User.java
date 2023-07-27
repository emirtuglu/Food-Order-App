package com.example;

import java.util.ArrayList;
import java.util.Objects;


public class User {
    private int id;
    private String name;
    private String surname;
    private String phoneNumber;
    private ArrayList<Address> addresses;

    public User() {
    }

    public User(int id, String name, String surname, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
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

    public ArrayList<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    
}
