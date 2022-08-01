package server.database.dataHandlers;

import server.database.MySQLHandler;
import server.logic.captcha.Captcha;
import server.logic.captcha.CaptchaHandler;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHandler {
    MySQLHandler dataBaseHandler;
    CaptchaHandler captchaHandler;

    public UserHandler(MySQLHandler dataBaseHandler, CaptchaHandler captchaHandler) {
        this.dataBaseHandler = dataBaseHandler;
        this.captchaHandler = captchaHandler;
    }

    public Captcha sendCaptcha() {
        int captchaID = this.captchaHandler.generateRandomCaptchaID();
        String getCaptcha = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getCaptchaImage");
        String query = getCaptcha + " " + captchaID;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String captchaImageAddress = resultSet.getString("captchaImageAddress");
                String captchaValue = resultSet.getString("captchaValue");
                return new Captcha(captchaImageAddress, captchaValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getInformation(String userName){
        String getPassword = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getInformation");
        String query = getPassword + " " + getStringFormat(userName);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    String thisLogin = resultSet.getString("thisLogin");
                    String password = resultSet.getString("password");
                    String collegeCode = resultSet.getString("collegeCode");
                    UserType userType = UserType.valueOf(resultSet.getString("userType"));
                    return new User(userType, collegeCode, thisLogin, userName, password);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getStatus(String username) {
        String status = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getStatus");
        String query = status + " " + getStringFormat(username);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("status");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getProfessorType(String username) {
        String status = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getProfessorType");
        String query = status + " " + getStringFormat(username);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePassword(String password, String userName) {
        String updatedData = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updatePassword");
        String query = String.format(updatedData, getStringFormat(password)) + " " + getStringFormat(userName);
        this.dataBaseHandler.updateData(query);
    }

    public void updateThisLogin(String thisLogin, String userName) {
        String updatedData = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateThisLogin");
        String query = String.format(updatedData, thisLogin) + " " + getStringFormat(userName);
        this.dataBaseHandler.updateData(query);
    }

    public void updateLastLogin(String lastLogin, String userName) {
        String updatedData = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateLastLogin");
        String query = String.format(updatedData, lastLogin) + " " + getStringFormat(userName);
        this.dataBaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }

}
