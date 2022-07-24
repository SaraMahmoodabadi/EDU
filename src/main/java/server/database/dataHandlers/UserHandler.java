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
        String getCaptcha = Config.getConfig(ConfigType.QUERY).getProperty("getCaptchaImage");
        String query = getCaptcha + " " + captchaID;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        try {
            String captchaImageAddress = resultSet.getString("captchaImageValue");
            String captchaValue = resultSet.getString("captchaValue");
            return new Captcha(captchaImageAddress, captchaValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getInformation(String userName){
        String getPassword = Config.getConfig(ConfigType.QUERY).getProperty("getPassword");
        String query = getPassword + " " + userName;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                String lastLogin = resultSet.getString("lastLogin");
                String password = resultSet.getString("password");
                String collegeCode = resultSet.getString("collegeCode");
                UserType userType = UserType.valueOf(resultSet.getString("userType"));
                User user = new User(userType, collegeCode, lastLogin, userName, password);
                return user;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getStatus(String username) {
        String status = Config.getConfig(ConfigType.QUERY).getProperty("getStatus");
        String query = status + " " + username;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("status");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getProfessorType(String username) {
        String status = Config.getConfig(ConfigType.QUERY).getProperty("getProfessorType");
        String query = status + " " + username;
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("type");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void updatePassword(String password, String userName) {
        String updatedData = Config.getConfig(ConfigType.QUERY).getProperty("updatePassword");
        String query = String.format(updatedData, password) + " " + userName;
        this.dataBaseHandler.updateData(query);
    }

    public void updateLastLogin(String lastLogin, String userName) {
        String updatedData = Config.getConfig(ConfigType.QUERY).getProperty("updateLastLogin");
        String query = String.format(updatedData, lastLogin) + " " + userName;
        this.dataBaseHandler.updateData(query);
    }

}
