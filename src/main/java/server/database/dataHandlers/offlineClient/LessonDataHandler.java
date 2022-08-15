package server.database.dataHandlers.offlineClient;

import server.database.MySQLHandler;
import shared.model.university.lesson.Day;
import shared.model.university.lesson.Lesson;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LessonDataHandler {
    private final MySQLHandler databaseHandler;

    public LessonDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public ArrayList<Lesson> getLessons(String username) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        ArrayList<String> studentLessons = getStudentLessons(username);
        for (String lesson : studentLessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode =  lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            lessons.add(getLesson(lessonCode));
        }
        return lessons;
    }

    private Lesson getLesson(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name, days, classTime, examTime", "lesson") +
                " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String classTime = resultSet.getString("classTime");
                String name = resultSet.getString("name");
                String examTime = resultSet.getString("examTime");
                String days = resultSet.getString("days");
                String daysArray = days.substring(1, days.length() - 1);
                ArrayList<Day> lessonDays = new ArrayList<>();
                for (String day : daysArray.split(", ")) {
                    lessonDays.add(Day.valueOf(day));
                }
                String plan = "days: " + days + ", time: " + classTime;
                return new Lesson(lessonCode, name, classTime, lessonDays, plan, examTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getStudentLessons(String username) {
        ArrayList<String> lessons = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessonsString = resultSet.getString("lessonsCode");
                if (lessonsString != null && !lessonsString.equals("[]")) {
                    String lessonsList = lessonsString.substring(1, lessonsString.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsList.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    private String getStringFormat(String string) {
        return "'" + string + "'";
    }
}
