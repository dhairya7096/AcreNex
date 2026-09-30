package com.acrenex.app.models;

public class User {

    private String id;
    private String name;
    private String email;
    private String state;
    private String district;

    public User() {
    }

    public User(
            String id,
            String name,
            String email,
            String state,
            String district
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.state = state;
        this.district = district;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}