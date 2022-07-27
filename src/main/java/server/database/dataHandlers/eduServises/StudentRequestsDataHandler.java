package server.database.dataHandlers.eduServises;

import server.database.MySQLHandler;
import shared.model.message.request.Type;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentRequestsDataHandler {
    private final MySQLHandler databaseHandler;

    public StudentRequestsDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public Grade getStudentGrade(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getStudentGrade");
        query = query + " " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return (Grade) resultSet.getObject("grade");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public List<String> getCertificateInfo(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getCertificateInformation");
        query = query + " " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String studentCode = resultSet.getString("studentCode");
                String college = resultSet.getString("name");
                return new ArrayList<>(Arrays.asList(firstname, lastname, studentCode, college));
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public boolean registerMinor(String items, String collegeCode, String major, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + username;
        String query2 = String.format(query, "educationalAssistantCode", "college") + " collegeCode = " + collegeCode;
        String query3 = String.format(query, "educationalAssistantCode", "college") + " name = " + major;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        ResultSet resultSet3 = this.databaseHandler.getResultSet(query3);
        if (resultSet1 != null && resultSet2 != null && resultSet3 != null) {
            String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty("registerRequest");
            try {
                finalQuery = String.format(finalQuery, items, resultSet1.getString("studentCode") +
                        ", " + resultSet2.getString("educationalAssistantCode") + ", " +
                        Type.MINOR + ", " + resultSet3.getString("educationalAssistantCode"));
                return this.databaseHandler.updateData(finalQuery);
            } catch (SQLException ignored) {}
        }
        return false;
    }

    public Double getRate(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        query = String.format(query, "rate", "student") +  " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getDouble("rate");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public boolean registerRecommendation(String items, String professorCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + username;
        String query2 = String.format(query, "username", "professor") + " professorCode = " + professorCode;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        if (resultSet1 != null && resultSet2 != null) {
            String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty("registerRequest");
            try {
                finalQuery = String.format(finalQuery, items, resultSet1.getString("studentCode") +
                        ", " + professorCode + ", " +
                        Type.RECOMMENDATION);
                return this.databaseHandler.updateData(finalQuery);
            } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean registerWithdrawal(String items, String collegeCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + username;
        String query2 = String.format(query, "educationalAssistantCode", "college") + " collegeCode = " + collegeCode;
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        if (resultSet1 != null && resultSet2 != null) {
            String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty("registerRequest");
            try {
                finalQuery = String.format(finalQuery, items, resultSet1.getString("studentCode") +
                        ", " +  resultSet2.getString("educationalAssistantCode") + ", " +
                        Type.WITHDRAWAL);
                return this.databaseHandler.updateData(finalQuery);
            } catch (SQLException ignored) {}
        }
        return false;
    }
}
