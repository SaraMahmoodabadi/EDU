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
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getStudentGrade");
        query = query + " " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Grade.valueOf(resultSet.getString("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getCertificateInfo(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getCertificateInformation");
        query = query + " " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstName");
                String lastname = resultSet.getString("lastName");
                String studentCode = resultSet.getString("studentCode");
                String college = resultSet.getString("name");
                return new ArrayList<>(Arrays.asList(firstname, lastname, studentCode, college));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerMinor(String items, String collegeCode, String major, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + getStringFormat(username);
        String query2 = String.format(query, "educationalAssistantCode", "college") + " collegeCode = "
                + getStringFormat(collegeCode);
        String query3 = String.format(query, "educationalAssistantCode", "college") + " name = " + getStringFormat(major);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        ResultSet resultSet3 = this.databaseHandler.getResultSet(query3);
        try {
            if (resultSet1.next() && resultSet2.next() && resultSet3.next()) {
                String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "registerRequest");
                finalQuery = String.format(finalQuery, items, getStringFormat(resultSet1.getString("studentCode")) +
                        ", " + getStringFormat(resultSet2.getString("educationalAssistantCode")) + ", " +
                        Type.MINOR + ", " + getStringFormat(resultSet3.getString("educationalAssistantCode")));
                return this.databaseHandler.updateData(finalQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Double getRate(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "rate", "student") +  " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Double.parseDouble(resultSet.getString("rate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean registerRecommendation(String items, String professorCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + getStringFormat(username);
        String query2 = String.format(query, "username", "professor") + " professorCode = " + getStringFormat(professorCode);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        try {
            if (resultSet1.next() && resultSet2.next()) {
                String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "registerRequest");
                finalQuery = String.format(finalQuery, items, getStringFormat(resultSet1.getString("studentCode")) +
                        ", " + getStringFormat(professorCode) + ", " +
                        getStringFormat(Type.RECOMMENDATION.toString()));
                return this.databaseHandler.updateData(finalQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerWithdrawal(String items, String collegeCode, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        String query1 = String.format(query, "studentCode", "student") +  " username = " + getStringFormat(username);
        String query2 = String.format(query, "educationalAssistantCode", "college") + " collegeCode = " +
                getStringFormat(collegeCode);
        ResultSet resultSet1 = this.databaseHandler.getResultSet(query1);
        ResultSet resultSet2 = this.databaseHandler.getResultSet(query2);
        try {
            if (resultSet1.next() && resultSet2.next()) {
                String finalQuery = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "registerRequest");
                finalQuery = String.format(finalQuery, items, getStringFormat(resultSet1.getString("studentCode")) +
                        ", " +  getStringFormat(resultSet2.getString("educationalAssistantCode")) + ", " +
                        getStringFormat(Type.WITHDRAWAL.toString()));
                return this.databaseHandler.updateData(finalQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
