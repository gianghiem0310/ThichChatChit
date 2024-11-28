package com.example.appchatchit.models;

public class Friend {
    private long id;
    private int status;

    public Friend() {
    }

    public Friend(long id, int status) {
        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
