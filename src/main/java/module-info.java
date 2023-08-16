module com.example.serverjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    opens com.example.serverjavafx to javafx.fxml;
    exports com.example.serverjavafx;
}