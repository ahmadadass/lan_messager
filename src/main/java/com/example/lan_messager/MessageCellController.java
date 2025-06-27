package com.example.lan_messager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageCellController {
    public Label l_right;
    public Label l_middle;
    public Label l_left;
    public HBox hb_message_container;

    public void setData(Message message, String style) {
        if (style.equals("RIGHR")) {
            hb_message_container.setStyle("-fx-orientation: LEFT_TO_RIGHT;");
        } else if (style.equals("LEFT")) {
            hb_message_container.setStyle("-fx-orientation: RIGHT_TO_LEFT;");
        }

        l_right.setStyle("-fx-font-weight: bold;");

        l_right.setText(message.getSender().getName());
        l_middle.setText(message.getMassage());
        l_left.setText(message.getTime());
    }

    public void setViewRight(){

    }
}

