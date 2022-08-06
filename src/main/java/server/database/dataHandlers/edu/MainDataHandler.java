package server.database.dataHandlers.edu;

import server.database.MySQLHandler;
import shared.model.user.User;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainDataHandler {
    private final MySQLHandler dataBaseHandler;

    public MainDataHandler(MySQLHandler dataHandler) {
        this.dataBaseHandler = dataHandler;
    }

    public User getMainPageData(String userName) {
        String information = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getMainPageData");
        String query = information + " " + getStringFormat(userName);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String email = resultSet.getString("emailAddress");
                String lastLogin = resultSet.getString("lastLogin");
                String image = resultSet.getString("imageAddress");
                return new User(firstName, lastName, email, lastLogin, image);
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public List<String> getTableData(String userName) {
        String information = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getMainPageTable");
        String query = information + " " + getStringFormat(userName);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String status = resultSet.getString("status");
                String registrationLicense = resultSet.getString("registrationLicense");
                String registrationTime = resultSet.getString("registrationTime");
                List<String> tableList = new ArrayList<>();
                tableList.add(status);
                tableList.add(firstName);
                tableList.add(lastName);
                tableList.add(registrationLicense);
                tableList.add(registrationTime);
                return tableList;
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public String getUnitSelectionTime(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "RegistrationTime", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("RegistrationTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }

}
