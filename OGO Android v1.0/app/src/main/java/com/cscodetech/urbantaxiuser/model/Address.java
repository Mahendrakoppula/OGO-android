package com.customerogo.app.model;

public class Address {
    String id;
    Double lats;
    Double longs;
    String title;
    String addres;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLats() {
        return lats;
    }

    public void setLats(Double lats) {
        this.lats = lats;
    }

    public Double getLongs() {
        return longs;
    }

    public void setLongs(Double longs) {
        this.longs = longs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }
}
