package server.database;

import server.network.ClientHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLHandler {
    private Connection connection;
    private ClientHandler client;

    public MySQLHandler(ClientHandler client) {
        this.client = client;
        register();
        createConnection();
    }

    public MySQLHandler() {
        register();
        createConnection();
    }

    private void register() {
        try {
            String className = Config.getConfig(ConfigType.MYSQL).getProperty(String.class, "className");
            Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            if (this.client != null)
                this.client.disconnect();
        }
    }

    private void createConnection() {
        try {
            String url = Config.getConfig(ConfigType.MYSQL).getProperty(String.class, "url");
            String user = Config.getConfig(ConfigType.MYSQL).getProperty(String.class, "user");
            String password = Config.getConfig(ConfigType.MYSQL).getProperty(String.class, "password");
            this.connection = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
            if (this.client != null)
                this.client.disconnect();
        }
    }

    public ResultSet getResultSet(String query) {
        try {
            Statement statement = this.connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            if (this.client != null)
                this.client.disconnect();
        }
        return null;
    }

    public boolean updateData(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (this.client != null)
                this.client.disconnect();
        }
        return false;
    }

    public boolean removeData(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (this.client != null)
                this.client.disconnect();
        }
        return false;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close connection!");
        }
    }

}
