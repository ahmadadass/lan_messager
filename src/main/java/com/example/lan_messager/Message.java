package com.example.lan_messager;

import static com.example.lan_messager.MainController.users;

public class Message {
    String massage;
    User sender;
    User receiver;
    String time;

    public Message(String massage, User sender, User thisUser, String time) {
        this.massage = massage;
        this.sender = sender;
        this.receiver = thisUser;
        this.time = time;
    }

    public String getMassage() {
        return this.massage;
    }

    public User getSender() {
        return this.sender;
    }

    public User getReceiver() {
        return this.receiver;
    }

    public User getSenderUserByIp(String ip){
        for(User user : users){
            if(user.getIp().equals(ip)){
                return user;
            }
        }
        return null;
    }

    public String getTime() {
        return this.time;
    }
}