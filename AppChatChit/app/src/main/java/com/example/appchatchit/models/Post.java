package com.example.appchatchit.models;

import java.util.ArrayList;

public class Post {
    private long id;
    private long idAccount;
    private String image;
    private String caption;
    private int type;
    private String date;

    public Post() {
    }

    public Post(long id, long idAccount, String image, String caption, int type, String date) {
        this.id = id;
        this.idAccount = idAccount;
        this.image = image;
        this.caption = caption;
        this.type = type;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(long idAccount) {
        this.idAccount = idAccount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
