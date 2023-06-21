package com.customerogo.app.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("fname")
    private String fname;

    @SerializedName("ccode")
    private String ccode;

    @SerializedName("lname")
    private String lname;

    @SerializedName("password")
    private String password;

    @SerializedName("wallet")
    private String wallet;

    @SerializedName("registartion_date")
    private String registartionDate;

    @SerializedName("parentcode")
    private Object parentcode;

    @SerializedName("refercode")
    private String refercode;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("status")
    private String status;

    public String getFname(){
        return fname;
    }

    public String getCcode(){
        return ccode;
    }

    public String getLname(){
        return lname;
    }

    public String getPassword(){
        return password;
    }

    public String getWallet(){
        return wallet;
    }

    public String getRegistartionDate(){
        return registartionDate;
    }

    public Object getParentcode(){
        return parentcode;
    }

    public String getRefercode(){
        return refercode;
    }

    public String getMobile(){
        return mobile;
    }

    public String getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getStatus(){
        return status;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public void setRegistartionDate(String registartionDate) {
        this.registartionDate = registartionDate;
    }

    public void setParentcode(Object parentcode) {
        this.parentcode = parentcode;
    }

    public void setRefercode(String refercode) {
        this.refercode = refercode;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}