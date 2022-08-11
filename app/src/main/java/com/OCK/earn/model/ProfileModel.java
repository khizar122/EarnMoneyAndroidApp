package com.OCK.earn.model;

public class ProfileModel {
private String name,email,image,phone;
private int coins ,spins;

    public ProfileModel()
    {

    }
    public ProfileModel(String name, String email,String image,String phone, int coins ,int spins) {
        this.name = name;
        this.email = email;
        this.coins = coins;
        this.image = image;
        this.spins = spins;
        this.phone=phone;
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
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }
}
