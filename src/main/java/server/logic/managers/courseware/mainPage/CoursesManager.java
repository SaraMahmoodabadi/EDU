package server.logic.managers.courseware.mainPage;

import server.database.MySQLHandler;
import server.database.dataHandlers.courseware.mainPage.CoursesDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.Course;
import shared.model.courseware.exercise.Exercise;
import shared.model.university.lesson.Group;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.ArrayList;
import java.util.List;

public class CoursesManager {
    private ClientHandler client;
    private final CoursesDataHandler dataHandler;

    public CoursesManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new CoursesDataHandler(clientHandler.getDataHandler());
    }

    public CoursesManager(MySQLHandler handler) {
        this.dataHandler = new CoursesDataHandler(handler);
    }

    public void createAllCollegeLessons(String collegeCode) {
        List<Group> groups = this.dataHandler.getAllCollegeLessons(collegeCode);
        for (Group group : groups) {
            if (isCreated(group.getLessonCode(), String.valueOf(group.getGroupNumber()))) continue;
            createCourse(group.getLessonCode(), String.valueOf(group.getGroupNumber()),
                    group.getProfessorCode(), group.getStudents());
        }
    }

    private void createCourse(String lessonCode, String group, String professorCode, List<String> students) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String courseCode = thisTerm + "-" + lessonCode + "-" + group;
        boolean result = this.dataHandler.createCourse(courseCode, professorCode, students);
        if (result) {
            this.dataHandler.updateProfessorCourses(professorCode, courseCode);
            for (String student : students) {
                this.dataHandler.updateStudentCourses(student, courseCode);
            }
        }
    }

    private boolean isCreated(String lessonCode, String group) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String courseCode = thisTerm + "-" + lessonCode + "-" + group;
        return this.dataHandler.isCreated(courseCode);
    }

    public Response getAllCourses() {
        ArrayList<String> courses = this.dataHandler.getAllCourses(this.client.getUserName(), this.client.getUserType());
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < courses.size(); i++) {
            String name = this.dataHandler.getLessonName(getLessonCode(courses.get(i)));
            if (name == null) continue;
            Course course = new Course(courses.get(i), name);
            response.addData("course" + i, course);
        }
        return response;
    }

    public Response getAllEvents(Request request) {
        String date = (String) request.getData("date");
        ArrayList<String> courses = this.dataHandler.getAllCourses
                (this.client.getUserName(), this.client.getUserType());
        ArrayList<String> events = new ArrayList<>();
        for (String course : courses) {
            ArrayList<Exercise> exercises = this.dataHandler.getCourseExercises(course);
            String lessonCode = getLessonCode(course);
            String lessonName = this.dataHandler.getLessonName(lessonCode);
            for (Exercise exercise : exercises) {
                String time = exercise.getUploadingTimeWithoutDeductingScores().substring(0, 10);
                if (!time.equals(date)) continue;
                String exerciseName = exercise.getName();
                String event = "course: " + lessonName + ", exercise: " + exerciseName + ", uploadTime: " +
                        exercise.getUploadingTimeWithoutDeductingScores();
                events.add(event);
            }
            String examTime = this.dataHandler.getExamTime(lessonCode);
            if (examTime != null && examTime.substring(0, 10).equals(date)) {
                String event = "course: " + lessonName + ", exam time: " + examTime;
                events.add(event);
            }
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < events.size(); i++) {
            response.addData("event" + i, events.get(i));
        }
        return response;
    }

    private String getLessonCode(String course) {
        String term = course.split("-")[0];
        int n = course.split("-").length;
        String group = course.split("-")[n - 1];
        return course.substring(term.length() + 1, course.length() - group.length() - 1);
    }
}
