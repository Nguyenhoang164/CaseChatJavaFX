module com.example.clientjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    opens com.example.clientjavafx to javafx.fxml;
    exports com.example.clientjavafx;
}