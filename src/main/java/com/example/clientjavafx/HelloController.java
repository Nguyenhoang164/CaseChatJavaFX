package com.example.clientjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.*;

public class HelloController {
    @FXML
    private Label Message;
    @FXML
    private TextField Input_Message;
    private Socket serverSocket;

    @FXML
    public void AddMessage() throws IOException, SQLException {
        ConnectedServer();
        String Message = this.Input_Message.getText();
        ImportToDatabase importToDatabase = new ImportToDatabase();
        String message = SendMessageToServer(Message);
        if (message.isEmpty()) {
            StopConnected();
            importToDatabase.DeleteAll();
        }
        importToDatabase.AddToDatabase(message);
        ReadFromServer();
        StringBuilder listMessage = importToDatabase.ReadMessage();
        this.Message.setText(String.valueOf(listMessage));
        this.Input_Message.setText("");
    }
   @FXML
   public void DeleteMessage() throws SQLException{
        ImportToDatabase importToDatabase = new ImportToDatabase();
        importToDatabase.DeleteAll();
        this.Message.setText("");
   }
    public void ConnectedServer() throws IOException {
        String localhost = "localhost";
        int ClientPort = 2121;
        serverSocket = new Socket(localhost, ClientPort);
        System.out.println("ket noi thanh cong voi server");
    }


    private String ReadFromServer() throws IOException {
        if (serverSocket != null && serverSocket.isConnected()) {
            InputStream inputStream = this.serverSocket.getInputStream();
            byte[] list = new byte[1024];
            int read = inputStream.read(list);
            String message = new String(list, 0, read);
            return message;
        }
        return "";
    }

    private String SendMessageToServer(String message) throws IOException {
        if (serverSocket != null && serverSocket.isConnected()) {
            OutputStream outputStream = this.serverSocket.getOutputStream();
            String messageLine = "Client : " + message;
            outputStream.write(messageLine.getBytes());
            outputStream.flush();
            return messageLine;
        }
        return "";
    }

    public void StopConnected() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}

class ImportToDatabase {
    private String localhost = "localhost:3306";
    private String dbname = "DataMessage";
    private String username = "root";
    private String password = "Kamito@123";
    private String URLConnect = "jdbc:mysql://" + localhost + "/" + dbname;

    public Connection ConnectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URLConnect, username, password);
        return connection;
    }

    public void AddToDatabase(String message) throws SQLException {
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String result = message;
        String query = "insert into Message(Message) values ('" + result + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void DeleteAll() throws SQLException {
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String query = "TRUNCATE TABLE Message";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public StringBuilder ReadMessage() throws SQLException {
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String query = "select Message from Message";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        StringBuilder newMessage = new StringBuilder();
        String message = null;
        while (resultSet.next()) {
            message = resultSet.getString("Message");
            newMessage.append(message + "\n");
        }
        return newMessage;

    }
}