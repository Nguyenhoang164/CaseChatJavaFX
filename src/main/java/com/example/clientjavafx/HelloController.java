package com.example.clientjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

public class HelloController {
    @FXML
    private Label Message;
    @FXML
    private TextField Input_Message;
    @FXML
    public void AddMessage() throws IOException, SQLException {
        HelloController helloController = new HelloController();
        String Message = this.Input_Message.getText();
        Socket socket = helloController.socket();
        ImportToDatabase importToDatabase = new ImportToDatabase();
        String message = SendMessageToServer(socket,Message);
        if (message.equals("exit")){
            importToDatabase.DeleteAll();
        }
        importToDatabase.AddToDatabase(message);
        ReadFromServer(socket);
        StringBuilder listMessage = importToDatabase.ReadMessage();
        this.Message.setText(String.valueOf(listMessage));
        this.Input_Message.setText("");

        }
        public Socket socket() throws IOException {
            String localhost = "localhost";
            int ClientPort = 8080;
            Socket socket = new Socket(localhost, ClientPort);
            return socket;
        }

    private static String ReadFromServer(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] list = new byte[1024];
        int read = inputStream.read(list);
        String message = new String(list, 0, read);
        return message;
    }
    private static String SendMessageToServer(Socket socket, String message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String messageLine = "Client : " + message;
        outputStream.write(messageLine.getBytes());
        outputStream.flush();
        return messageLine;
    }
}
class ImportToDatabase{
    private String localhost = "localhost:3306";
    private String dbname = "DataMessage";
    private String username = "root";
    private String password = "Kamito@123";
    private String URLConnect ="jdbc:mysql://"+ localhost+"/"+dbname;
    public Connection ConnectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URLConnect,username,password);
        System.out.println("Kết nối database thành công ");
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
    public StringBuilder ReadMessage() throws SQLException{
        ImportToDatabase importToDatabase = new ImportToDatabase();
        Connection connection = importToDatabase.ConnectToDatabase();
        String query = "select Message from Message";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        StringBuilder newMessage = new StringBuilder();
        String message = null;
        while (resultSet.next()){
            message = resultSet.getString("Message");
            newMessage.append(message + "\n");
        }
        return newMessage;

    }
}