package server.database.dataHandlers.eduServises;

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
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getUserLessons");
        query = String.format(query, userType) + " " + userType + "Code = " +
                getStringFormat(getUserCode(userType, username));
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    String lessons = resultSet.getString("lessonsCode");
                    if (lessons != null) {
                        String lessonArray = lessons.substring(1, lessons.length() - 1);
                        return getLessonsWeeklyPlan(new ArrayList<>(Arrays.asList(lessonArray.split(", "))));
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private List<Lesson> getLessonsWeeklyPlan(List<String> lessonsCode) {
        List<Lesson> lessons = new ArrayList<>();
        for (String lessonCode : lessonsCode) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty
                    (String.class, "getWeeklyPlan") + " " + getStringFormat(lessonCode);
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            try {
                if (resultSet.next()) {
                    String classTime = resultSet.getString("classTime");
                    String name = resultSet.getString("name");
                    String days = resultSet.getString("days");
                    String daysArray = days.substring(1, days.length() - 1);
                    List<Day> lessonDays = new ArrayList<>();
                    for (String day : daysArray.split(", ")) {
                        lessonDays.add(Day.valueOf(day));
                    }
                    Lesson lesson = new Lesson(lessonCode, name, lessonDays, classTime);
                    lessons.add(lesson);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lessons;
    }

    private String getUserCode(String userType, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, userType + "Code", userType) + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString(userType + "Code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Lesson> getUserExams(String userType, String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getUserLessons");
        query = String.format(query, userType) + userType + "Code = " + getStringFormat(getUserCode(userType, username));
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("lessonsCode");
                String lessonArray = lessons.substring(1, lessons.length() - 1);
                return getLessonsExam(new ArrayList<>(Arrays.asList(lessonArray.split(", "))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Lesson> getLessonsExam(List<String> lessonsCode) {
        List<Lesson> lessons = new ArrayList<>();
        for (String lessonCode : lessonsCode) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getExam")
                    + " " + getStringFormat(lessonCode);
            ResultSet resultSet = this.databaseHandler.getResultSet(query);
            try {
                if (resultSet.next()) {
                    String examTime = resultSet.getString("examTime");
                    String name = resultSet.getString("name");
                    Lesson lesson = new Lesson(lessonCode, name, examTime);
                    lessons.add(lesson);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lessons;
    }

    public String getCollegeCode(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "collegeCode", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
