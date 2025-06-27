package com.example.lan_messager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.lan_messager.MainController.*;

public class ProfileController implements Initializable {

    @FXML public Label l_ip;
    @FXML public TextField tf_username;

    @FXML Parent root;
    @FXML Scene scene;
    @FXML Stage stage;


    int userindxe = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        l_ip.setText("Ip: " + thisUser.getIp());
        tf_username.setText(thisUser.getName());
        userindxe = FindUserIndexByIp(thisUser.getIp());
    }
/*
    public void setData(String ip, String username, int index){
        Platform.runLater(() -> {
            l_ip.setText(ip);
            tf_username.setText(username);
        });
        userindxe = index;
    }*/

    public void UpdateProfile(ActionEvent event) throws IOException {
        if (!tf_username.getText().isEmpty()){
            if (userindxe == 0) {
                users.get(userindxe).setName(tf_username.getText());
                thisUser.setName(tf_username.getText());
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));

            root = loader.load();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("Username is empty");
        }
    }

}
