package server.database.dataHandlers;

import server.database.MySQLHandler;
import shared.model.university.lesson.Day;
import shared.model.university.lesson.Lesson;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanDataHandler {
    private final MySQLHandler databaseHandler;

    public PlanDataHandler(MySQLHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public List<Lesson> getUserWeeklyPlan(String userType, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getUserLessons");
        query = String.format(query, userType) + userType + "Code = " + getUserCode(userType, username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                Array lessons = resultSet.getArray("lessonsCode");
                String[] lessonsCode = (String[]) lessons.getArray();
                return getLessonsWeeklyPlan(Arrays.asList(lessonsCode));
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private List<Lesson> getLessonsWeeklyPlan(List<String> lessonsCode) {
        List<Lesson> lessons = new ArrayList<>();
        for (String lessonCode : lessonsCode) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty("getWeeklyPlan") + " " + lessonCode;
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            if (resultSet != null) {
                try {
                    String classTime = resultSet.getString("classTime");
                    String name = resultSet.getString("name");
                    Array days = resultSet.getArray("days");
                    Day[] lessonDays = (Day[]) days.getArray();
                    Lesson lesson = new Lesson(name, Arrays.asList(lessonDays), classTime);
                    lessons.add(lesson);
                } catch (SQLException ignored) {
                }
            }
        }
        return lessons;
    }

    private String getUserCode(String userType, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty("getOneData");
        query = String.format(query, userType + "Code", userType) + " username = " + username;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                return resultSet.getString(userType + "Code");
            } catch (SQLException ignored) {}
        }
        return null;
    }


}
