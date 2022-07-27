package server.database.dataHandlers.eduServises;

import server.database.MySQLHandler;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfessorRequestDataHandler {
    private final MySQLHandler databaseHandler;

    public ProfessorRequestDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public boolean registerRecommendationAnswer(String studentCode, String firstBlank,
                                                String secondBlank, String thirdBlank, String username) {
        if (!isValidProfessorCode(studentCode, username)) return false;
        String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty("updateData");
        finalQuery = String.format(finalQuery, "request",  "firstBlank = " + firstBlank +
                ", secondBlank = " + secondBlank +
                ", thirdBlank = " + thirdBlank) + " studentCode = " + studentCode;
        return this.databaseHandler.updateData(finalQuery);
    }

    public boolean registerRequestAnswer(String studentCode, String username, boolean result, boolean isMinor) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("updateData");
        String request = String.format(query, "request", "result = " + result) + " studentCode = " + studentCode;
        if (isMinor) {
            if (isValidProfessorCode(studentCode, username)) {
                query = request;
            }
            else if (isAnotherCollege(studentCode, username)) {
                query = String.format(query, "request", "secondResult = " + result) + " studentCode = " + studentCode;
            }
            else return false;
        }
        else {
            if (isValidProfessorCode(studentCode, username))
                query = request;
        }
        return this.databaseHandler.updateData(query);
    }

    private boolean isValidProfessorCode(String studentCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        String query1 = String.format(query, "professorCode", "student") +  " studentCode = " + studentCode;
        String query2 = String.format(query, "professorCode", "professor") + " username = " + username;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        if (resultSet1 != null && resultSet2 != null) {
            try {
                if (resultSet1.getString("professorCode").equals
                        (resultSet2.getString("professorCode"))) return true;
            } catch (SQLException ignored) {}
        }
        return false;
    }

    private boolean isAnotherCollege(String studentCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        String query1 = String.format(query, "professorCode", "student") +  " studentCode = " + studentCode;
        String query2 = String.format(query, "professorCode", "professor") + " username = " + username;
        String query3 = String.format(query, "anotherCollegeProfessorCode", "student") +  " studentCode = " + studentCode;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        ResultSet resultSet3 = this.databaseHandler.getResultSet(query3);
        if (resultSet1 != null && resultSet2 != null) {
            try {
                if (resultSet3.getString("anotherCollegeProfessorCode").equals
                        (resultSet2.getString("professorCode"))) return true;
            } catch (SQLException ignored) {}
        }
        return false;
    }

}
