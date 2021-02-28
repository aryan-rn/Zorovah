package com.example.zorovah.models;

import java.util.Objects;

public class chatModel {
    String sender,reciever,message,timestamp;
    boolean isSeen;
    public chatModel(String sender,String reciever,String message,String timestamp,boolean isSeen){
        this.sender=sender;
        this.reciever=reciever;
        this.message=message;
        this.timestamp=timestamp;
        this.isSeen=isSeen;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getReciever() {
        return reciever;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isSeen() {
        return isSeen;
    }


}
