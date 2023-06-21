package com.customerogo.app.model;

public class NearCar {
    String id;
    String image;
    Double lats;
    Double logs;
    String name;
    String type;
    String carimage;
    String orderid;
    boolean isavailable;
    String request_step;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getLats() {
        return lats;
    }

    public void setLats(Double lats) {
        this.lats = lats;
    }

    public Double getLogs() {
        return logs;
    }

    public void setLogs(Double logs) {
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getCarimage() {
        return carimage;
    }

    public void setCarimage(String carimage) {
        this.carimage = carimage;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public boolean isIsavailable() {
        return isavailable;
    }

    public void setIsavailable(boolean isavailable) {
        this.isavailable = isavailable;
    }

    public String getRequest_step() {
        return request_step;
    }

    public void setRequest_step(String request_step) {
        this.request_step = request_step;
    }
}
