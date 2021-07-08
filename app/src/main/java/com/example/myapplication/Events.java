package com.example.myapplication;

public class Events {

    private String image="";
    private String uid="";
    private String title="";
    private String date="";
    private String city="";
    private String fee="";

    public Events() {
    }

    public Events(String image, String uid, String title, String date, String city, String fee) {
        this.image = image;
        this.uid = uid;
        this.title = title;
        this.date = date;
        this.city = city;
        this.fee = fee;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
