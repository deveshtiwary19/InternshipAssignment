package com.example.myapplication;

public class Place {

    private String uid="";
    private String image="";
    private String city="";

    public Place() {
    }

    public Place(String uid, String image, String city) {
        this.uid = uid;
        this.image = image;
        this.city = city;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
