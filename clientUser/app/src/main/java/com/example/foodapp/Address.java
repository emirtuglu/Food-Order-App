package com.example.foodapp;

import java.util.Objects;

public class Address {
    private int id;
    private String title;
    private String city;
    private String district;
    private String fullAddress;

    public Address() {
    }

    public Address(int id, String city, String district, String fullAddress) {
        this.id = id;
        this.city = city;
        this.district = district;
        this.fullAddress = fullAddress;
    }

    public Address(int id, String title, String city, String district, String fullAddress) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.district = district;
        this.fullAddress = fullAddress;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFullAddress() {
        return this.fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Address)) {
            return false;
        }
        Address address = (Address) o;
        return id == address.id && Objects.equals(city, address.city) && Objects.equals(district, address.district) && Objects.equals(fullAddress, address.fullAddress);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", city='" + getCity() + "'" +
                ", district='" + getDistrict() + "'" +
                ", fullAddress='" + getFullAddress() + "'" +
                "}";
    }
}

