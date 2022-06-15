package ru.edu.bsu.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML
    private ListView textChat;

    @FXML
    private TextField sendText;

    ObservableList<String> messages = FXCollections.observableArrayList();
    @FXML
    private void onSendMessage() {
        messages.add(sendText.getText());
        textChat.setItems(messages);
        sendText.clear();

    }

    @FXML
    public void buttonPressed(KeyEvent e)
    {
        if(e.getCode().toString().equals("ENTER"))
        {
            onSendMessage();
        }
    }
}