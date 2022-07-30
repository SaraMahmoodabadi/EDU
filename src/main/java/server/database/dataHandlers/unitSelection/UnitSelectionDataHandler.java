package server.database.dataHandlers.unitSelection;

import server.database.MySQLHandler;
import shared.model.university.lesson.Lesson;
import shared.model.university.lesson.score.Score;
import shared.model.university.lesson.score.ScoreType;
import shared.model.user.student.Grade;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitSelectionDataHandler {
    private final MySQLHandler databaseHandler;

    public UnitSelectionDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean updateUnitSelectionTime(String items, String collegeCode, String time) {
        List<String> students = getStudentCodes(collegeCode);
        for (String student : students) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
            query = String.format(query, "student", "registrationTime = " + getStringFormat(time)) + items +
            " AND studentCode = " + getStringFormat(student);
            if (!this.databaseHandler.updateData(query)) return false;
        }
        return true;
    }

    private List<String> getStudentCodes(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "s.studentCode", "student s", "user u", "u.username  = s.username") +
                " " + "u.collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<String> studentsCode = new ArrayList<>();
        try {
            while (resultSet.next()) {
                studentsCode.add(resultSet.getString("studentCode"));
            }
        } catch (SQLException ignored) {}
        return studentsCode;
    }

    public List<Lesson> getCollegeLesson(String collegeName) {
        String collegeCode = getCollegeCode(collegeName);
        if (collegeCode == null) return null;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "l.lessonCode, l.name, g.groupNumber, l.examTime, l.grade",
                "lesson l", "group g", "l.lessonCode = g.lessonCode") + " l.collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<Lesson> lessons = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String lessonCode = resultSet.getString("lessonCode");
                String name  = resultSet.getString("name");
                int group = resultSet.getInt("groupNumber");
                String examTime = resultSet.getString("examTime");
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                Lesson lesson = new Lesson(lessonCode, name, examTime, grade, group);
                lessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    private String getCollegeCode(String collegeName) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "collegeCode", "college") + " name = " + getStringFormat(collegeName);
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

    public List<String> getStudentLessons(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("lessonsCode");
                String lessonsArray = lessons.substring(1, lessons.length() - 1);
                return new ArrayList<>(Arrays.asList(lessonsArray.split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } //semester-lessonCode-group

    public List<String> getStudentMarkedLessons(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "markedLessons", "student") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("markedLessons");
                String lessonsArray = lessons.substring(1, lessons.length() - 1);
                return new ArrayList<>(Arrays.asList(lessonsArray.split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } //lessonCode-group

    public List<Lesson> getSuggestedLessons(String items) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "l.lessonCode, l.name, g.groupNumber, l.examTime, l.grade",
                "lesson l", "group g", "l.lessonCode = g.lessonCode") + " " + items;
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<Lesson> lessons = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String lessonCode = resultSet.getString("lessonCode");
                String name  = resultSet.getString("name");
                int group = resultSet.getInt("groupNumber");
                String examTime = resultSet.getString("examTime");
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                Lesson lesson = new Lesson(lessonCode, name, examTime, grade, group);
                lessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    public List<Score> getStudentScores(String username) {
        String studentCode = getStudentCode(username);
        if (studentCode == null) return null;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "score") + " studentCode = " + getStringFormat(studentCode) +
                " AND type = " + getStringFormat(String.valueOf(ScoreType.FINAL));
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<Score> scores = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String scoreValue = resultSet.getString("score");
                String lessonCode = resultSet.getString("lessonCode");
                Score score = new Score(lessonCode, studentCode, scoreValue);
                scores.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
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
        return null;
    }

    public boolean markLesson(String lesson, String username) {
        List<String> lessons = getStudentMarkedLessons(username);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.add(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "markedLessons = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public boolean unmarkedLesson(String lesson, String username) {
        List<String> lessons = getStudentMarkedLessons(username);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.remove(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "markedLessons = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public boolean removeLesson(String lesson, String username) {
        List<String> lessons = getStudentLessons(username);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.remove(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "lessonsCode = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public boolean takeLesson(String lesson, String username) {
        List<String> lessons = getStudentLessons(username);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.add(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "lessonsCode = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
