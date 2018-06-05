package com.example.yas.andr1project;

/**
 * Created by YAS on 3/16/2018.
 */

public class Building {

    private String id;
    private String name;
    private String description;
    private String address;
    private String postalCode;

    public Building(String id, String name, String description,String address,String postalCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address=address;
        this.postalCode=postalCode;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getPostalCode() {return postalCode;}

    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
}
