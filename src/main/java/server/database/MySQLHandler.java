package server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLHandler {
    private Connection connection;

    public MySQLHandler() {
        register();
        createConnection();
    }

    private void register() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(ClassNotFoundException ex) {
            System.out.println("Error: unable to load driver class!");
            System.exit(1);
        }
    }

    private void createConnection() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/edu","root","sara1381");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getData(String query, List<String> columnName) {
        List<String> dataList = new ArrayList<>();
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String data = generateData(resultSet, columnName);
                dataList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private String generateData(ResultSet resultSet, List<String> columnName) {
        StringBuilder data = new StringBuilder();
        int columnCount = 0;
        for (String name : columnName) {
            columnCount++;
            try {
                data.append(resultSet.getString(name));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (columnCount < columnName.size()) data.append(", ");
        }
        return data.toString();
    }

    public void updateData(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close connection!");
        }
    }

}
