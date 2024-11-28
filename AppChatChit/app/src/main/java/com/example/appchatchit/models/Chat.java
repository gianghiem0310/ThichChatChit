package com.example.appchatchit.models;

public class Chat {
    private long id;
    private long idAccount1;
    private long idAccount2;
    private String newMessage;
    private String timeMessage;
    private boolean statusAccount1;
    private boolean statusAccount2;
    public Chat() {
    }

    public Chat(long id, long idAccount1, long idAccount2, String newMessage, String timeMessage, boolean statusAccount1, boolean statusAccount2) {
        this.id = id;
        this.idAccount1 = idAccount1;
        this.idAccount2 = idAccount2;
        this.newMessage = newMessage;
        this.timeMessage = timeMessage;
        this.statusAccount1 = statusAccount1;
        this.statusAccount2 = statusAccount2;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdAccount1() {
        return idAccount1;
    }

    public void setIdAccount1(long idAccount1) {
        this.idAccount1 = idAccount1;
    }

    public long getIdAccount2() {
        return idAccount2;
    }

    public void setIdAccount2(long idAccount2) {
        this.idAccount2 = idAccount2;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }

    public boolean isStatusAccount1() {
        return statusAccount1;
    }

    public void setStatusAccount1(boolean statusAccount1) {
        this.statusAccount1 = statusAccount1;
    }

    public boolean isStatusAccount2() {
        return statusAccount2;
    }

    public void setStatusAccount2(boolean statusAccount2) {
        this.statusAccount2 = statusAccount2;
    }
}
