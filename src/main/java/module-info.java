module ru.edu.bsu.chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.edu.bsu.chat to javafx.fxml;
    exports ru.edu.bsu.chat;
}