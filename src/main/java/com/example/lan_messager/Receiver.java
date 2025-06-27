package com.example.lan_messager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.example.lan_messager.MainController.*;

public class Receiver implements Runnable{

    public static void Receiver(Label l_username, TextField tf_main, TableView<User> tv_users, TableColumn<User, String> tc_users, TableColumn<User, String> tc_number_of_messages, ListView<Message> lv_messages, ObservableList<User> userList, ObservableList<Message> userMessages) throws IOException {
        DatagramSocket socket = new DatagramSocket(9876);
        byte[] buffer = new byte[1024];

        //MainController mainController = new MainController();

        while (true) {
            System.out.println("Waiting for broadcast...");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);


            String message = new String(packet.getData(), 0, packet.getLength());
            InetAddress senderAddress = packet.getAddress();

            //int senderPort = packet.getPort();

            if (message.contains("DISCOVER_PEER_My_Name_Is: ")) {
                System.out.println("Discovery message received from: " + senderAddress.getHostAddress());

                // Send back confirmation

                String reply = "Hi_My_Name_Is: " + thisUser.getName();

                byte[] replyData = reply.getBytes();
                DatagramPacket response = new DatagramPacket(replyData, replyData.length, senderAddress, 9876);
                socket.send(response);

                System.out.println("Replied to: " + senderAddress.getHostAddress());
                if (FindUserByIp(senderAddress.getHostAddress()) == null && !thisUser.ip.equals(senderAddress.getHostAddress())) {
                    users.add(new User(message.substring(27), senderAddress.getHostAddress()));
                    System.out.println("new ip found: " + senderAddress.getHostAddress());
                    UpdateUsers(tv_users,userList);
                }
            } else if (message.contains("Hi_My_Name_Is: ")) {
                if (FindUserByIp(senderAddress.getHostAddress()) == null && !thisUser.ip.equals(senderAddress.getHostAddress())){
                    users.add(new User(message.substring(15), senderAddress.getHostAddress()));
                    System.out.println("new ip found: " + senderAddress.getHostAddress());
                    UpdateUsers(tv_users,userList);
                }
            } else {
                System.out.println("Discovery message received from: " + senderAddress.getHostAddress());
                System.out.println("message: " + message);
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                if (FindUserByIp(senderAddress.getHostAddress()) == null && !thisUser.ip.equals(senderAddress.getHostAddress())){
                    users.add(new User(senderAddress.getHostAddress(), senderAddress.getHostAddress()));
                    System.out.println("new ip found: " + senderAddress.getHostAddress());
                    UpdateUsers(tv_users,userList);
                }
                if (!senderAddress.getHostAddress().equals(thisUser.ip)) {
                    messages.add(new Message(message, FindUserByIp(senderAddress.getHostAddress()), thisUser,currentTime));
                    UpdateMessages(userMessages, lv_messages, senderAddress.getHostAddress());
                    if (currentUser == null || !currentUser.ip.equals(senderAddress.getHostAddress())) {
                        users.get(FindUserIndexByIp(senderAddress.getHostAddress())).newMessageNumber++;
                    }
                }
                UpdateUsers(tv_users, userList);
            }
        }
    }

    @Override public void run() {
        try {
            Receiver(l_username,tf_main,tv_users,tc_users,tc_number_of_messages,lv_messages,userList,userMessages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Label l_username;
    private final TextField tf_main;
    private final TableView<User> tv_users;
    private final TableColumn<User, String> tc_users;
    private final TableColumn<User, String> tc_number_of_messages;
    private final ListView<Message> lv_messages;
    private final ObservableList<User> userList;
    private final ObservableList<Message> userMessages;

    public Receiver(Label l_username,
                            TextField tf_main,
                            TableView<User> tv_users,
                            TableColumn<User, String> tc_users,
                            TableColumn<User, String> tc_number_of_messages,
                            ListView<Message> lv_messages,
                            ObservableList<User> userList,
                            ObservableList<Message> userMessages) {
        this.l_username = l_username;
        this.tf_main = tf_main;
        this.tv_users = tv_users;
        this.tc_users = tc_users;
        this.tc_number_of_messages = tc_number_of_messages;
        this.lv_messages = lv_messages;
        this.userList = userList;
        this.userMessages = userMessages;
    }
}
