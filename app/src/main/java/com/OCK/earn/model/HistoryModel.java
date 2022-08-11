package com.OCK.earn.model;

public class HistoryModel {
    String id;
    String phone;
    String status;
    String name;
    int amount;
    String image;
    String date;

    public HistoryModel() {
    }

    public HistoryModel(String id, String phone,String status, String name, int amount, String image,String date) {
        this.id = id;
        this.phone = phone;
        this.status = status;
        this.name = name;
        this.amount = amount;
        this.image = image;
        this.date=date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
