package com.example.lan_messager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML Label l_username;
    @FXML TextField tf_main;
    @FXML TableView<User> tv_users;
    @FXML TableColumn<User, String> tc_users;
    @FXML TableColumn<User, String> tc_number_of_messages;
    @FXML ListView<Message> lv_messages;
    @FXML Parent root;
    @FXML Scene scene;
    @FXML Stage stage;

    static ArrayList<User> users = new ArrayList<User>();
    static ArrayList<Message> messages = new ArrayList<Message>();
    static User currentUser;
    static User thisUser;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Message> userMessages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            if (thisUser == null) {
                thisUser = new User(String.valueOf(Inet4Address.getLocalHost().getHostAddress()), String.valueOf(Inet4Address.getLocalHost().getHostAddress()));
                users.add(thisUser);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        tc_users.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_number_of_messages.setCellValueFactory(new PropertyValueFactory<>("newMessageNumber"));

        tc_users.setStyle("-fx-alignment: CENTER;");
        tc_number_of_messages.setStyle("-fx-alignment: CENTER;");

        Thread threadReceiver = new Thread(new Receiver(l_username, tf_main, tv_users, tc_users, tc_number_of_messages, lv_messages, userList, userMessages));
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
                    users.get(FindUserIndexByIp(currentUser.ip)).newMessageNumber = 0;
                    Platform.runLater(() -> {
                        UpdateUsers(tv_users, userList);
                    });
                    UpdateMessages(userMessages, lv_messages, selectedUser.getIp());
                    System.out.println("User clicked: " + currentUser.ip);
                }
            }
        });

        lv_messages.setCellFactory(listView -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("message_cell.fxml"));
                        Parent root = loader.load();
                        MessageCellController controller = loader.getController();
                        if (message.sender.equals(currentUser)) {
                            controller.setData(message, "RIGHT");
                        } else {
                            controller.setData(message, "LEFT");
                        }
                        setGraphic(root); // 👈 This displays the FXML inside the ListView
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        l_username.setText(thisUser.getName());
        UpdateUsers(tv_users, userList);
        UpdateMessages(userMessages, lv_messages, "0");
    }

    public void Search() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true); // enable broadcast

        String message = "DISCOVER_PEER_My_Name_Is: " + thisUser.getName();
        byte[] buffer = message.getBytes();

        // Send to the broadcast address of your LAN (example: 192.167.1.255)
        InetAddress broadcastAddress = InetAddress.getByName("192.167.1.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 9876);

        socket.send(packet);
        System.out.println("Broadcast message sent.");
        socket.close();
    }

    public static void UpdateUsers(TableView<User> tv_users, ObservableList<User> userList){
        userList.clear();
        userList.addAll(users);
        tv_users.setItems(userList);
        tv_users.setEditable(false);
    }

    public static void UpdateMessages(ObservableList<Message> userMessages, ListView<Message> lv_messages, String senderAddress) {
        if (currentUser != null && !messages.isEmpty()) {
            Platform.runLater(() -> {
                userMessages.clear();
            });
            for (Message message : messages) {
                if (message.getSender().equals(currentUser) && message.getReceiver().equals(thisUser) || message.getReceiver().equals(currentUser) && message.getSender().equals(thisUser)) {
                    Platform.runLater(() -> {
                        userMessages.add(message);
                    });
                }
            }
        }
        lv_messages.setItems(userMessages);
    }

    public void Send() throws Exception {
        if (currentUser != null && !tf_main.getText().isEmpty() && tf_main.getText().length() <= 50) {
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            messages.add(new Message(tf_main.getText(),thisUser,currentUser,currentTime));
            SendMessage(currentUser.ip, tf_main.getText());
            UpdateMessages(userMessages, lv_messages, currentUser.getIp());
            tf_main.setText("");
        } else {
            System.out.println("No user selected\nPlease full all fields\nNo more than 50 characters");
        }
    }

    public void Profile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profile_view.fxml"));

        root = loader.load();

        //ProfileController profileController = new ProfileController();
        //profileController.setData(thisUser.getIp(), thisUser.getName(), 0);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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

    public static User FindUserByIp(String ip) {
        if (users != null) {
            for (User user : users) {
                if (user.getIp().equals(ip)) {
                    return user;
                }
            }
        }
        return null;
    }

    public static int FindUserIndexByIp(String ip) {
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getIp().equals(ip)) {
                    return i;
                }
            }
        }
        return 0;
    }

}