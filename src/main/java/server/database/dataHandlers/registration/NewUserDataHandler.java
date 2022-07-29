package server.database.dataHandlers.registration;

import server.database.MySQLHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewUserDataHandler {
    private final MySQLHandler dataBaseHandler;

    public NewUserDataHandler(MySQLHandler handler) {
        this.dataBaseHandler = handler;
    }

    public boolean makeNewUser(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeNewUser");
        query = String.format(query, items);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean makeNewProfessor(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeNewProfessor");
        query = String.format(query, items);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean makeNewStudent(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "makeNewStudent");
        query = String.format(query, items);
        return this.dataBaseHandler.updateData(query);
    }

    public boolean existProfessorCode(String professorCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existUserData");
        query = String.format(query, "username", "professorCode = " + professorCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("username") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean existStudentCode(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existUserData");
        query = String.format(query, "username", "professorCode = " + studentCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("username") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean existUsername(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existUserData");
        query = String.format(query, "firstName", "username = " + username);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("firstName") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean existNationalCode(String nationalCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "existUserData");
        query = String.format(query, "username", "nationalCode = " + nationalCode);
        ResultSet resultSet = this.dataBaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.getString("username") != null) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
