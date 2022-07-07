package ru.edu.bsu.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatController {

    @FXML
    private ListView textChat;

    @FXML
    private TextField sendText;

    ObservableList<String> messages = FXCollections.observableArrayList();

    private final ChatClient client;

    public ChatController() {
        this.client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            } catch (IOException e) {
                showNotification();
            }
        }

    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно установить соединение с сервером",
                new ButtonType("Повторить", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Выход", ButtonBar.ButtonData.CANCEL_CLOSE)
        );
        alert.setTitle("Ошибка подключения!");
        final Optional<ButtonType> answer = alert.showAndWait();
        final Boolean isExit = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if (isExit) {
            System.exit(0);
        }
    }

    @FXML
    private void onSendMessage() {
        client.sendMessage(sendText.getText());
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

    public void addMessage(String message) {
        messages.add(message);
        textChat.setItems(messages);
    }
}