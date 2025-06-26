package com.example.lan_messager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageCellController {
    @FXML private Label l_senderUsername;
    @FXML private Label l_message;
    @FXML private Label l_time;

    public void setData(Message message) {
        l_senderUsername.setText(message.getSender().getName());
        l_message.setText(message.getMassage());
        l_time.setText(message.getTime());
    }

    public void setViewRight(){

    }
}

