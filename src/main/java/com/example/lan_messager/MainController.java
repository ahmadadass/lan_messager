package com.example.lan_messager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML Label l_username;
    @FXML TextField tf_main;
    @FXML TableView<User> tv_users;
    @FXML TableColumn<User, String> tc_users;
    @FXML TableColumn<User, String> tc_number_of_messages;
    @FXML ListView<Message> lv_messages;

    static ArrayList<User> users = new ArrayList<User>();
    static ArrayList<Message> messages = new ArrayList<Message>();
    static User currentUser;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Message> userMessages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //users.add(new User("ahmad", "192.167.1.3"));
        //users.add(new User("saeed", "192.167.1.2"));

        tc_users.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_number_of_messages.setCellValueFactory(new PropertyValueFactory<>("newMessageNumber"));

        tc_users.setStyle("-fx-alignment: CENTER;");
        tc_number_of_messages.setStyle("-fx-alignment: CENTER;");

        Thread threadReceiver = new Thread(new Receiver(l_username,tf_main,tv_users,tc_users,tc_number_of_messages,lv_messages,userList, userMessages));
        threadReceiver.start();

        try {
            Search();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tv_users.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tv_users.setOnMouseClicked(event -> {
            if (!tv_users.getItems().isEmpty()) {
                User selectedUser = tv_users.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    currentUser = selectedUser;
                    UpdateMessages(userMessages, lv_messages);
                    System.out.println("User clicked: " + currentUser.ip);
                }
            }
        });

    }

    public void Search() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true); // enable broadcast

        String message = "DISCOVER_PEER";
        byte[] buffer = message.getBytes();

        // Send to the broadcast address of your LAN (example: 192.167.1.255)
        InetAddress broadcastAddress = InetAddress.getByName("192.167.1.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 9876);

        socket.send(packet);
        System.out.println("Broadcast message sent.");
        socket.close();
    }

    public static void UpdateUsers(TableView<User> tv_users, ObservableList<User> userList){
        userList.addAll(users);
        tv_users.setItems(userList);
        tv_users.setEditable(false);
    }

    public static void UpdateMessages(ObservableList<Message> userMessages, ListView<Message> lv_messages) {
        if (currentUser != null && !messages.isEmpty()) {
            if (messages.getLast().getSender().equals(currentUser.getIp())) {
                for (Message message : messages) {
                    if (message.getSender().equals(currentUser.getIp())) {
                        userMessages.add(message);
                    }
                }
            } else {
                // TODO notfay user of a new message
            }
        }

        lv_messages.setItems(userMessages);
    }

    public void Send() throws Exception {
        if (currentUser != null) {
            SendMessage(currentUser.ip, tf_main.getText());
        } else {
            System.out.println("No user selected");
        }
    }

    public void Profile(){}

    public static void SendMessage(String ip, String message) throws Exception{
        DatagramSocket socket = new DatagramSocket();

        //String message = "Nigga!";
        byte[] buffer = message.getBytes();

        // Replace with the IP address of the receiving PC
        InetAddress receiverAddress = InetAddress.getByName(ip);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 9876);
        socket.send(packet);

        System.out.println("Message sent.");
        socket.close();
    }

    public static Boolean FindUserByIp(String ip) {
        if (users != null) {
            for (User user : users) {
                if (user.getIp().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }

}