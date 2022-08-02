package server.database.dataHandlers.unitSelection;

import server.database.MySQLHandler;
import shared.model.message.request.Type;
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

    public boolean updateUnitSelectionTime(String grade, String year, String collegeCode, String time) {
        List<String> students = getStudentCodes(collegeCode);
        setTimeInTable(time);
        for (String student : students) {
            String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
            query = String.format(query, "student", "registrationTime = " + getStringFormat(time)) +
                 " grade = " + getStringFormat(grade) + " AND enteringYear = " + getStringFormat(year) +
                    " AND studentCode = " + getStringFormat(student);
            if (!this.databaseHandler.updateData(query)) return false;
        }
        return true;
    }

    private void setTimeInTable(String time) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "registrationtimes", "time", getStringFormat(time));
        this.databaseHandler.updateData(query);
    }

    public List<String> getStudentCodes(String collegeCode) {
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

    public List<String> getStudentCodes() {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "studentCode", "student");
        ResultSet resultSet = this.databaseHandler.getResultSet(query.substring(0, query.length() - 6));
        List<String> studentsCode = new ArrayList<>();
        try {
            while (resultSet.next()) {
                studentsCode.add(resultSet.getString("studentCode"));
            }
        } catch (SQLException ignored) {}
        return studentsCode;
    }

    public String getRegistrationTime(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "registrationTime", "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("registrationTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCollegeRegistrationTime(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "endUnitSelectionTime", "college") + " collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("endUnitSelectionTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void finalCollegeRegistration(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "college", "isUnitSelectionFinaled = 'true'")
                + " collegeCode = " + getStringFormat(collegeCode);
        this.databaseHandler.updateData(query);
    }

    public boolean isFinaledCollegeRegistration(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "isUnitSelectionFinaled", "college") +
                " collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Boolean.parseBoolean(resultSet.getString("registrationFinaled"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getCollegeCodes() {
        List<String> colleges = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "collegeCode", "college");
        ResultSet resultSet = this.databaseHandler.getResultSet(query.substring(0, query.length() - 6));
        try {
            while (resultSet.next()) {
               colleges.add(resultSet.getString("collegeCode"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colleges;
    }

    public void finalRegistration(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "registrationFinaled = 'true'") + " studentCode = " + getStringFormat(studentCode);
        this.databaseHandler.updateData(query);
    }

    public boolean IsFinaledRegistration(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "registrationFinaled", "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return Boolean.parseBoolean(resultSet.getString("registrationFinaled"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public List<String> getStudentLessons(String studentCode, boolean isStudentCode) {
        if (!isStudentCode)
            studentCode = getStudentCode(studentCode);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("lessonsCode");
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
                else return new ArrayList<>();
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
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
                else return new ArrayList<>();
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
        List<String> lessons = getStudentLessons(username, false);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.remove(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "lessonsCode = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public boolean takeLesson(String lesson, String username) {
        List<String> lessons = getStudentLessons(username, false);
        if (lessons == null) lessons = new ArrayList<>();
        lessons.add(lesson);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "lessonsCode = " + getStringFormat(lessons.toString())) +
                " username = " + getStringFormat(username);
        return this.databaseHandler.updateData(query);
    }

    public Grade getStudentGrade(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "grade", "student") + " username = " + getStringFormat(username);
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

    public void removeStudentFromGroup(String lessonCode, String group, String username) {
        String studentCode = getStudentCode(username);
        List<String> students = getGroupStudents(lessonCode, group);
        if (students != null) students.remove(studentCode);
        else students = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "group", "students = " + getStringFormat(students.toString()))
                + " lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(group);
        this.databaseHandler.updateData(query);
    }

    public void addStudentToGroup(String lessonCode, String group, String username) {
        String studentCode = getStudentCode(username);
        List<String> students = getGroupStudents(lessonCode, group);
        if (students == null) students = new ArrayList<>();
        students.add(studentCode);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "group", "students = " + getStringFormat(students.toString()))
                + " lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(group);
        this.databaseHandler.updateData(query);
    }

    public List<String> getGroupStudents(String lessonCode, String group) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "students", "group") + " lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(group);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("students");
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
                else return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getGroupCapacity(String lessonCode, String group) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "capacity", "group") + " lessonCode = " + getStringFormat(lessonCode) +
                " AND groupNumber = " + getStringFormat(group);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
               return resultSet.getInt("capacity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Lesson getLesson(String lessonCode, int group) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name, examTime, grade", "lesson") +
                " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String name  = resultSet.getString("name");
                String examTime = resultSet.getString("examTime");
                Grade grade = Grade.valueOf(resultSet.getString("grade"));
                return new Lesson(lessonCode, name, examTime, grade, group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getPrerequisites(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "prerequisites", "lesson") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("prerequisites");
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
                else return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getNeeds(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "theNeed", "lesson") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("theNeed");
                if (lessons != null) {
                    String lessonsArray = lessons.substring(1, lessons.length() - 1);
                    return new ArrayList<>(Arrays.asList(lessonsArray.split(", ")));
                }
                else return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLessonData(String item, String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, item, "lesson") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean requestGetLesson(String username, String lessonCode) {
        String studentCode = getStudentCode(username);
        String collegeCode = getCollege(username);
        String professorCode = getAssistant(collegeCode);
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "request", "studentCode, professorCode, type, information" ,
                getStringFormat(studentCode) + ", " + getStringFormat(professorCode) + ", " + Type.TAKE_LESSON + ", " +
                lessonCode);
        return this.databaseHandler.updateData(query);
    }

    private String getAssistant(String collegeCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "educationalAssistantCode", "college") +
                " collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("educationalAssistantCode");
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public String getCollege(String username) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "collegeCode", "user") + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("collegeCode");
            }
        } catch (SQLException ignored) {}
        return null;
    }

    public List<String> getGroups(String lessonCode) {
        List<String> groups = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "groupNumber", "group") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            while (resultSet.next()) {
                groups.add(resultSet.getString("groupNumber"));
            }
        } catch (SQLException ignored) {}
        return groups;
    }

    public void updateStudentLessons(String studentCode, List<String> lessons) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", "lessonsCode = " + getStringFormat(lessons.toString()))
                + " studentCode = " + getStringFormat(studentCode);
        this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
