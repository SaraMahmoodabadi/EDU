package server.database.dataHandlers.score;

import server.database.MySQLHandler;
import shared.model.university.lesson.score.Score;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EDUStatusDataHandler {
    private final MySQLHandler databaseHandler;

    public EDUStatusDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public Map<Score, Integer> getFinalScores(String studentCode) {
        TemporaryScoresDataHandler dataHandler = new TemporaryScoresDataHandler(this.databaseHandler);
        return dataHandler.getFinalScores(studentCode);
    }

    public String getStudentCode(String studentName, String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.studentCode, u.firstName, u.lastName",
                "student s", "user u", "u.username = s.username")
                +  " u.collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    String firstname = resultSet.getString("firstName");
                    String lastname = resultSet.getString("lastName");
                    String studentCode = resultSet.getString("studentCode");
                    if (studentName.equals(firstname + " " + lastname)) return studentCode;
                }
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public String getCollegeCode(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "u.collegeCode", "student s", "user u", "u.username = s.username")
                +  " s.studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("collegeCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getLessons(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student")
                +  " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("lessonsCode");
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStudentCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student") +  " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("studentCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getRate(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "rate", "student") +  " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Double.parseDouble(resultSet.getString("rate"));
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
