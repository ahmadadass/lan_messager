package com.example.lan_messager;

public class User {
    String name;
    String ip;
    int newMessageNumber;

    public User(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return this.name;
    }

    public String getIp() {
        return this.ip;
    }

    public int getNewMessageNumber(){
        return this.newMessageNumber;
    }
}
