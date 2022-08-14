package server.database.dataHandlers.courseware.course;

import server.database.MySQLHandler;
import shared.model.courseware.educationalMaterial.EducationalMaterial;
import shared.model.courseware.exercise.Exercise;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class CourseDataHandler {
    private final MySQLHandler databaseHandler;

    public CourseDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public String getUserCode(String userType, String username) {
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
        return "";
    }

    public String getUsername(String studentCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "username", "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<EducationalMaterial> getEduMaterials(String courseCode) {
        ArrayList<EducationalMaterial> educationalMaterials = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "educationalMaterials", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String eduMaterials = resultSet.getString("educationalMaterials");
                if (eduMaterials != null && !eduMaterials.equals("[]")) {
                    String list = eduMaterials.substring(1, eduMaterials.length() - 1);
                    ArrayList<String> eduMaterialList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    for (String eduMaterial : eduMaterialList) {
                        EducationalMaterial educationalMaterial = getEduMaterial(eduMaterial, courseCode);
                        if (educationalMaterial != null) educationalMaterials.add(educationalMaterial);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return educationalMaterials;
    }

    private EducationalMaterial getEduMaterial(String eduMaterialCode, String courseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name, time", "educational_material") +
                " educationalMaterialCode = " + getStringFormat(eduMaterialCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String time = resultSet.getString("time");
                return new EducationalMaterial(courseCode, eduMaterialCode, name, time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Exercise> getExercises(String courseCode) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "exercises", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String exercisesString = resultSet.getString("exercises");
                if (exercisesString != null && !exercisesString.equals("[]")) {
                    String list = exercisesString.substring(1, exercisesString.length() - 1);
                    ArrayList<String> exerciseList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    for (String exerciseCode : exerciseList) {
                        Exercise exercise = getExercise(exerciseCode, courseCode);
                        if (exercise != null) exercises.add(exercise);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    private Exercise getExercise(String exerciseCode, String courseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name, openingTime, closingTime", "exercise") +
                " exerciseCode = " + getStringFormat(exerciseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String openingTime = resultSet.getString("openingTime");
                String closingTime = resultSet.getString("closingTime");
                return new Exercise(courseCode, exerciseCode, name, openingTime, closingTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getAssistants(String courseCode) {
        ArrayList<String> assistants = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "teacherAssistants", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String teacherAssistants = resultSet.getString("teacherAssistants");
                if (teacherAssistants!= null && !teacherAssistants.equals("[]")) {
                    String list = teacherAssistants.substring(1, teacherAssistants.length() - 1);
                    ArrayList<String> assistantList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    assistants.addAll(assistantList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assistants;
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

    public boolean updateCourseStudents(String studentCode, String courseCode, String type) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, type, "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<String> students = new ArrayList<>();
        try {
            if (resultSet.next()) {
                String studentsString = resultSet.getString(type);
                if (studentsString != null && !studentsString.equals("[]")) {
                    String list = studentsString.substring(1, studentsString.length() - 1);
                    ArrayList<String> studentsList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    students.addAll(studentsList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!students.contains(studentCode)) students.add(studentCode);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "course", type + " = " + getStringFormat(students.toString()))
                + " courseCode = " + getStringFormat(courseCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean updateStudentCourses(String studentCode, String courseCode, String type) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, type, "student") + " studentCode = " + getStringFormat(studentCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<String> courses = new ArrayList<>();
        try {
            if (resultSet.next()) {
                String coursesString = resultSet.getString(type);
                if (coursesString != null && !coursesString.equals("[]")) {
                    String list = coursesString.substring(1, coursesString.length() - 1);
                    ArrayList<String> coursesList = new ArrayList<>(Arrays.asList(list.split(", ")));
                    courses.addAll(coursesList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!courses.contains(courseCode)) courses.add(courseCode);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "student", type + " = " + getStringFormat(courses.toString()))
                + " studentCode = " + getStringFormat(studentCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean addEduMaterial(String eduMaterialCode, String name) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "educational_material", " educationalMaterialCode, name, time",
                getStringFormat(eduMaterialCode) + ", " + getStringFormat(name) + ", " +
                getStringFormat(LocalDateTime.now().toString()));
        return this.databaseHandler.updateData(query);
    }

    public boolean addExercise(String exerciseCode, String name) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "exercise", " exerciseCode, name",
                getStringFormat(exerciseCode) + ", " + getStringFormat(name));
        return this.databaseHandler.updateData(query);
    }

    public boolean updateCourseExercises(String courseCode, String exerciseCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "exercises", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<String> exerciseList = new ArrayList<>();
        try {
            if (resultSet.next()) {
                String exercisesString = resultSet.getString("exercises");
                if (exercisesString != null && !exercisesString.equals("[]")) {
                    String list = exercisesString.substring(1, exercisesString.length() - 1);
                    exerciseList = new ArrayList<>(Arrays.asList(list.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!exerciseList.contains(exerciseCode)) exerciseList.add(exerciseCode);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "course", " exercises = " + getStringFormat(exerciseList.toString()))
                + " courseCode = " + getStringFormat(courseCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean updateCourseEduMaterials(String courseCode, String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "educationalMaterials", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<String> eduMaterialList = new ArrayList<>();
        try {
            if (resultSet.next()) {
                String eduMaterials = resultSet.getString("educationalMaterials");
                if (eduMaterials != null && !eduMaterials.equals("[]")) {
                    String list = eduMaterials.substring(1, eduMaterials.length() - 1);
                    eduMaterialList = new ArrayList<>(Arrays.asList(list.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!eduMaterialList.contains(eduMaterialCode)) eduMaterialList.add(eduMaterialCode);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "course", " educationalMaterials = " + getStringFormat(eduMaterialList.toString()))
                + " courseCode = " + getStringFormat(courseCode);
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
