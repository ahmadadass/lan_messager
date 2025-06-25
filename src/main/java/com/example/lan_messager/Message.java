package com.example.lan_messager;

import static com.example.lan_messager.MainController.users;

public class Message {
    String massage;
    String sender;
    String time;

    public Message(String massage, String sender, String time) {
        this.massage = massage;
        this.sender = sender;
        this.time = time;
    }

    public String getMassage() {
        return this.massage;
    }

    public String getSender() {
        return this.sender;
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