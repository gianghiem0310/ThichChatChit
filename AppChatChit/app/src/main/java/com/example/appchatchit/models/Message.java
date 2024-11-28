package com.example.appchatchit.models;

public class Message {
    private long id;
    private long idSender;
    private long idReceiver;
    private String content;
    private String date;

    public Message() {
    }

    public Message(long id, long idSender, long idReceiver, String content, String date) {
        this.id = id;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.content = content;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdSender() {
        return idSender;
    }

    public void setIdSender(long idSender) {
        this.idSender = idSender;
    }

    public long getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(long idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
