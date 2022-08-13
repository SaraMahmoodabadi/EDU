package server.database.dataHandlers.courseware.course.exercise;

import server.database.MySQLHandler;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Exercise;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentExerciseDataHandler {
    private final MySQLHandler databaseHandler;

    public StudentExerciseDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Exercise getExercise(String exerciseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "exercise") + " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String openTime = resultSet.getString("openingTime");
                String closeTime = resultSet.getString("closingTime");
                String uploadTime = resultSet.getString("uploadingTimeWithoutDeductingScores");
                String fileAddress = resultSet.getString("fileAddress");
                String descriptions = resultSet.getString("descriptions");
                ItemType type = ItemType.valueOf(resultSet.getString("itemType"));
                return new Exercise(exerciseCode, name, openTime, closeTime, uploadTime,
                        descriptions, fileAddress, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasSubmitted(String exerciseCode, String userName) {
        String studentCode = getStudentCode(userName);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "answer") + " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
               String student = resultSet.getString("studentCode");
               if (student.equals(studentCode)) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getScore(String exerciseCode, String userName) {
        String studentCode = getStudentCode(userName);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "score", "answer") + " exerciseCode = " + getStringFormat(exerciseCode) +
                " AND studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
               return resultSet.getString("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    private String getStudentCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("studentCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
