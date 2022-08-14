package server.database.dataHandlers.courseware.mainPage;

import server.database.MySQLHandler;
import shared.model.courseware.exercise.Exercise;
import shared.model.university.lesson.Group;
import shared.model.user.UserType;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoursesDataHandler {
    private final MySQLHandler databaseHandler;

    public CoursesDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public List<Group> getAllCollegeLessons(String collegeCode) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getDataWithJoin");
        query = String.format(query, "l.lessonCode, g.groupNumber, g.students, g.professorCode", "lesson l",
                "edu.group g", "g.lessonCode = l.lessonCode") +
                " l.term = " + thisTerm + " AND l.collegeCode = " + getStringFormat(collegeCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        List<Group> groups = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String lessonCode = resultSet.getString("lessonCode");
                String group = resultSet.getString("groupNumber");
                String professorCode = resultSet.getString("professorCode");
                String students = resultSet.getString("students");
                if (students == null || students.equals("[]")) {
                    Group newGroup = new Group(lessonCode, group, professorCode, new ArrayList<>());
                    groups.add(newGroup);
                }
                else {
                    String list = students.substring(1, students.length() - 1);
                    List<String> studentList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    ArrayList<String> finalList = new ArrayList<>();
                    for (String student : studentList)
                        if (hasStudentThisLesson(student, thisTerm, lessonCode, group)) finalList.add(student);
                    Group newGroup = new Group(lessonCode, group, professorCode, finalList);
                    groups.add(newGroup);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private boolean hasStudentThisLesson(String studentCode, String term, String lessonCode, String group) {
        String lesson = term + "-" + lessonCode + "-" + group;
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "lessonsCode", "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String lessons = resultSet.getString("lessonsCode");
                if (lessons == null || lessons.equals("[]")) return false;
                String list = lessons.substring(1, lessons.length() - 1);
                ArrayList<String> lessonsList = new ArrayList<>(Arrays.asList(list.split(", ")));
                if (lessonsList.contains(lesson)) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createCourse(String courseCode, String professorCode, List<String> students) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "course", "courseCode, professorCode, students",
                getStringFormat(courseCode) + ", " + getStringFormat(professorCode) + ", " +
                getStringFormat(students.toString()));
        return this.databaseHandler.updateData(query);
    }

    public boolean isCreated(String courseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "professorCode", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                if (resultSet.getString("professorCode") != null) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateStudentCourses(String student, String courseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "courses", "student") + " studentCode = " + getStringFormat(student);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                ArrayList<String> coursesList;
                String courses = resultSet.getString("courses");
                if (courses == null || courses.equals("[]")) coursesList = new ArrayList<>();
                else {
                    String list = courses.substring(1, courses.length() - 1);
                    coursesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                }
                coursesList.add(courseCode);
                query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
                query = String.format(query, "student", "courses = " + getStringFormat(coursesList.toString()))
                        + " studentCode = " + getStringFormat(student);
                this.databaseHandler.updateData(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProfessorCourses(String professor, String courseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "courses", "professor") + " professorCode = " + getStringFormat(professor);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                ArrayList<String> coursesList;
                String courses = resultSet.getString("courses");
                if (courses == null || courses.equals("[]")) coursesList = new ArrayList<>();
                else {
                    String list = courses.substring(1, courses.length() - 1);
                    coursesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                }
                coursesList.add(courseCode);
                query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
                query = String.format(query, "professor", "courses = " + getStringFormat(coursesList.toString()))
                        + " professorCode = " + getStringFormat(professor);
                this.databaseHandler.updateData(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllCourses(String username, UserType userType) {
        ArrayList<String> allCourses = new ArrayList<>();
        String user = userType.toString().toLowerCase();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "courses", user) + " username = " + getStringFormat(username);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String courses = resultSet.getString("courses");
                if (courses != null && !courses.equals("[]")) {
                    String list = courses.substring(1, courses.length() - 1);
                    ArrayList<String> coursesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    allCourses.addAll(coursesList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (userType == UserType.STUDENT) {
            query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
            query = String.format(query, "assistantCourses", "student") + " username = " + getStringFormat(username);
            resultSet = this.databaseHandler.getResultSet(query);
            try {
                if (resultSet.next()) {
                    String courses = resultSet.getString("assistantCourses");
                    if (courses != null && !courses.equals("[]")) {
                        String list = courses.substring(1, courses.length() - 1);
                        ArrayList<String> coursesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                        allCourses.addAll(coursesList);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allCourses;
    }

    public String getLessonName(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name", "lesson") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                if (resultSet.getString("name") != null)
                    return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Exercise> getCourseExercises(String course) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "exercises", "course") + " courseCode = " + getStringFormat(course);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String courseExercises = resultSet.getString("exercises");
                if (courseExercises != null && !courseExercises.equals("[]")) {
                    String list = courseExercises.substring(1, courseExercises.length() - 1);
                    ArrayList<String> exercisesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    for (String exercise : exercisesList) {
                        Exercise courseExercise = getExercise(exercise);
                        if (courseExercise != null) exercises.add(courseExercise);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    private Exercise getExercise(String exerciseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name, uploadingTimeWithoutDeductingScores", "exercise")
                + " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String time = resultSet.getString("uploadingTimeWithoutDeductingScores");
                return new Exercise(exerciseCode, name, time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getExamTime(String lessonCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "examTime", "lesson") + " lessonCode = " + getStringFormat(lessonCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                if (resultSet.getString("examTime") != null)
                    return resultSet.getString("examTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateSystemMessages(String username, String message) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "system_messages", "sender, receiver, time, message",
                "Courseware, " + getStringFormat(username) + ", " + getStringFormat(LocalDateTime.now().toString()) +
                ", " + getStringFormat(message));
        this.databaseHandler.updateData(query);
    }

    public String getUsername(String type, String userCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "username", type) + " " + type + "Code = " + getStringFormat(userCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("username");
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
