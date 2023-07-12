package com;
import java.util.Objects;


public class Restaurant {
    private int id;
    private Address address;
    private String name;
    private String phoneNumber;
    private String mail;

    public Restaurant() {
    }

    public Restaurant(int id, Address address, String name, String phoneNumber, String mail) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getaddress() {
        return this.address;
    }

    public void setaddress(Address address) {
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Restaurant)) {
            return false;
        }
        Restaurant restaurant = (Restaurant) o;
        return id == restaurant.id && address == restaurant.address && Objects.equals(name, restaurant.name) && Objects.equals(phoneNumber, restaurant.phoneNumber) && Objects.equals(mail, restaurant.mail);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", address='" + getaddress() + "'" +
            ", name='" + getName() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", mail='" + getMail() + "'" +
            "}";
    }
}
