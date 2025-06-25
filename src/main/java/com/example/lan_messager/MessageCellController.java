package com.example.lan_messager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageCellController {
    @FXML private Label senderLabel;
    @FXML private Label messageLabel;

    public void setData(Message message) {
        senderLabel.setText(message.getSender());
        messageLabel.setText(message.getMassage());
    }
}

