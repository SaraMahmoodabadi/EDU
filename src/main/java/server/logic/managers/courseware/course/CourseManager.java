package server.logic.managers.courseware.course;

import server.database.dataHandlers.courseware.course.CourseDataHandler;
import server.database.dataHandlers.edu.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.educationalMaterial.EducationalMaterial;
import shared.model.courseware.exercise.Exercise;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseManager {
    private final ClientHandler client;
    private final CourseDataHandler dataHandler;

    public CourseManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new CourseDataHandler(clientHandler.getDataHandler());
    }

    public Response showCourse(Request request) {
        String courseCode = (String) request.getData("courseCode");
        String timeType = (String) request.getData("time");
        ArrayList<EducationalMaterial> eduMaterials = getEduMaterials(courseCode);
        ArrayList<Exercise> exercises = getExercises(courseCode, timeType);
        boolean isAssistant = isAssistant(courseCode);
        Response response = new Response(ResponseStatus.OK);
        response.addData("isAssistant", isAssistant);
        response.addData("educationalMaterialSize", eduMaterials.size());
        response.addData("exerciseSize", exercises.size());
        for (int i = 0; i < eduMaterials.size(); i++) {
            response.addData("educationalMaterial" + i, eduMaterials.get(i));
        }
        for (int i = 0; i < exercises.size(); i++) {
            response.addData("exercise" + i, exercises.get(i));
        }
        return response;
    }

    public boolean isAssistant(String courseCode) {
        ArrayList<String> assistants = this.dataHandler.getAssistants(courseCode);
        if (this.client.getUserType() == UserType.STUDENT) {
            String studentCode = this.dataHandler.getUserCode("student", this.client.getUserName());
            return assistants.contains(studentCode);
        }
        return false;
    }

    private ArrayList<EducationalMaterial> getEduMaterials(String courseCode) {
        ArrayList<EducationalMaterial> eduMaterials = this.dataHandler.getEduMaterials(courseCode);
        ArrayList<String> times = new ArrayList<>();
        for (EducationalMaterial educationalMaterial : eduMaterials) times.add(educationalMaterial.getTime());
        ArrayList<String> sortedTimes = getSortedTimes(times);
        ArrayList<EducationalMaterial> finalList = new ArrayList<>();
        for (String time : sortedTimes) {
            for (EducationalMaterial educationalMaterial : eduMaterials) {
                if (educationalMaterial.getTime().equals(time)) {
                    finalList.add(educationalMaterial);
                }
            }
        }
        return finalList;
    }

    private ArrayList<Exercise> getExercises(String courseCode, String timeType) {
        ArrayList<Exercise> exercises = this.dataHandler.getExercises(courseCode);
        ArrayList<String> times = new ArrayList<>();
        for (Exercise exercise : exercises) {
            String time;
            if (exercise.getOpeningTime() == null || exercise.getClosingTime() == null) continue;
            if (timeType.equals("openingTime")) {
                time = exercise.getOpeningTime().substring(0, 10) + "T" +
                        exercise.getOpeningTime().substring(11, 16) + ":00.000";
            }
            else {
                time = exercise.getClosingTime().substring(0, 10) + "T" +
                        exercise.getClosingTime().substring(11, 16) + ":00.000";
            }
            times.add(time);
        }
        ArrayList<String> sortedTimes = getSortedTimes(times);
        ArrayList<Exercise> finalList = new ArrayList<>();
        for (String time : sortedTimes) {
            for (Exercise exercise : exercises) {
                String exerciseTime;
                if (exercise.getOpeningTime() == null || exercise.getClosingTime() == null) continue;
                if (timeType.equals("openingTime")) {
                    exerciseTime = exercise.getOpeningTime().substring(0, 10) + "T" +
                            exercise.getOpeningTime().substring(11, 16) + ":00.000";
                }
                else {
                    exerciseTime = exercise.getClosingTime().substring(0, 10) + "T" +
                            exercise.getClosingTime().substring(11, 16) + ":00.000";
                }
                if (exerciseTime.equals(time)) {
                    if (!finalList.contains(exercise)) finalList.add(exercise);
                }
            }
        }
        return finalList;
    }

    public Response getCourseEvents(Request request) {
        String date = (String) request.getData("date");
        String courseCode = (String) request.getData("courseCode");
        ArrayList<Exercise> exercises = this.dataHandler.getCourseExercises(courseCode);
        ArrayList<String> events = new ArrayList<>();
        for (Exercise exercise : exercises) {
            String time = exercise.getUploadingTimeWithoutDeductingScores().substring(0, 10);
            if (!time.equals(date)) continue;
            String exerciseName = exercise.getName();
            String event = "exercise: " + exerciseName + ", uploadTime: " +
                    exercise.getUploadingTimeWithoutDeductingScores();
            events.add(event);
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < events.size(); i++) {
            response.addData("event" + i, events.get(i));
        }
        return response;
    }

    public Response addStudent(Request request) {
        String studentType = (String) request.getData("studentType");
        String studentCode = (String) request.getData("studentCode");
        String courseCode = (String) request.getData("courseCode");
        String type = studentType.equals("student") ? "students" : "teacherAssistants";
        boolean result1 = this.dataHandler.updateCourseStudents(studentCode, courseCode, type);
        type = studentType.equals("student") ? "courses" : "assistantCourses";
        boolean result2 = this.dataHandler.updateStudentCourses(studentCode, courseCode, type);
        if (result1 && result2) {
            if (studentType.equals("student")) addStudentToLesson(studentCode, courseCode);
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private void addStudentToLesson(String studentCode, String lesson) {
        String username = this.dataHandler.getUsername(studentCode);
        String term = lesson.split("-")[0];
        int n = lesson.split("-").length;
        String group = lesson.split("-")[n - 1];
        String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
        UnitSelectionDataHandler handler = new UnitSelectionDataHandler(this.client.getDataHandler());
        handler.takeLesson(lesson, username);
        handler.addStudentToGroup(lessonCode, group, username);
    }

    public Response addEduMaterial(Request request) {
        String courseCode = (String) request.getData("courseCode");
        String name = (String) request.getData("name");
        String eduMaterialCode = courseCode + "-" + name;
        boolean result1 = this.dataHandler.addEduMaterial(eduMaterialCode, name);
        boolean result2 = this.dataHandler.updateCourseEduMaterials(courseCode, eduMaterialCode);
        if (result1 && result2) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    public Response addExercise(Request request) {
        String courseCode = (String) request.getData("courseCode");
        String name = (String) request.getData("name");
        String exerciseCode = courseCode + "-" + name;
        boolean result1 = this.dataHandler.addExercise(exerciseCode, name);
        boolean result2 = this.dataHandler.updateCourseExercises(courseCode, exerciseCode);
        if (result1 && result2) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private ArrayList<String> getSortedTimes(List<String> times) {
        String[] sortedTimes = new String[times.size()];
        for (int i = 0; i < times.size(); i++) {
            int t = 0;
            LocalDateTime date1 = LocalDateTime.parse(times.get(i));
            for (String time : times) {
                LocalDateTime date2 = LocalDateTime.parse(time);
                if (date1.isBefore(date2)) t++;
            }
            sortedTimes[t] = times.get(i);
        }
        return new ArrayList<>(Arrays.asList(sortedTimes));
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
