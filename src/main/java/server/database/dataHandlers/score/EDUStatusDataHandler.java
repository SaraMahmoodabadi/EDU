package server.database.dataHandlers.score;

import server.database.MySQLHandler;
import shared.model.university.lesson.score.Score;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                +  " u.collegeCode = " + collegeCode;
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
                +  " s.studentCode = " + studentCode;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("collegeCode");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public List<String> getLessons(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student")
                +  " studentCode = " + studentCode;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                Array array = resultSet.getArray("lessonsCode");
                String[] lessonsList = (String[]) array.getArray();
                return Arrays.asList(lessonsList);
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public String getStudentCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student")
                +  " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString("studentCode");
            } catch (SQLException ignored) {}
        }
        return null;
    }

    public Double getRate(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "rate", "student")
                +  " studentCode = " + studentCode;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return Double.parseDouble(resultSet.getString("rate"));
            } catch (SQLException ignored) {}
        }
        return null;
    }
}
