package com.appbuildersworld.zedtourerjava.models;

import java.io.Serializable;

public class MCashier implements Serializable {
    private int cashierId;
    private String cashierNames;
    private String phone;
    private String gender;
    private String password;
    private int businessId;
    private String imageUrl;

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierNames() {
        return cashierNames;
    }

    public void setCashierNames(String cashierNames) {
        this.cashierNames = cashierNames;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
