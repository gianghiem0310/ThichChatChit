package com.example.appchatchit.models;

public class Profile {
    private long idAccount;
    private String name;
    private String address;
    private String email;
    private String avatar;
    private String background;
    private boolean status;
    private int type;
    private int gender;

    public Profile() {
    }

    public Profile(long idAccount, String name, String address, String email, String avatar, String background, boolean status, int type, int gender) {
        this.idAccount = idAccount;
        this.name = name;
        this.address = address;
        this.email = email;
        this.avatar = avatar;
        this.background = background;
        this.status = status;
        this.type = type;
        this.gender = gender;
    }

    public long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(long idAccount) {
        this.idAccount = idAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
