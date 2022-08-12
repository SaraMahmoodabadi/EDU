package server.logic.managers.edu.unitSelection;

import server.database.MySQLHandler;
import server.database.dataHandlers.edu.unitSelection.UnitSelectionDataHandler;
import server.logic.managers.courseware.mainPage.CoursesManager;
import server.network.ClientHandler;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UnitSelectionTimeManager {
    private final UnitSelectionDataHandler dataHandler;

    public UnitSelectionTimeManager(ClientHandler client) {
        this.dataHandler = new UnitSelectionDataHandler(client.getDataHandler());
    }

    public Response setTime(Request request) {
        String grade = (String) request.getData("grade");
        String year = (String) request.getData("year");
        String time = (String) request.getData("time");
        String collegeCode = (String) request.getData("collegeCode");
        boolean result = this.dataHandler.updateUnitSelectionTime(grade, year, collegeCode, time);
        if (result) return sendOKResponse();
        else return sendErrorResponse();
    }

    public Response setEndTime(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        String time = (String) request.getData("time");
        boolean result = this.dataHandler.updateEndUnitSelectionTime(collegeCode, time);
        if (result) return sendOKResponse();
        else return sendErrorResponse();
    }

    public static void checkTime(MySQLHandler handler) {
        UnitSelectionDataHandler dataHandler = new UnitSelectionDataHandler(handler);
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    List<String> colleges = dataHandler.getCollegeCodes();
                    for (String college : colleges) {
                        if (isPassed(dataHandler.getCollegeRegistrationTime(college)) &&
                                !dataHandler.isFinaledCollegeRegistration(college)) {
                            List<String> students = dataHandler.getStudentCodes(college);
                            for (String student : students) {
                                checkLessons(student, handler);
                            }
                            makeCourses(college, handler);
                            dataHandler.finalCollegeRegistration(college);
                        }
                    }
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException ignored) {}
            }
        });
        thread.start();
    }

    protected static boolean isPassed(String time) {
        try {
            int y = Integer.parseInt(time.split("-")[0]);
            int m = Integer.parseInt(time.split("-")[1]);
            int d = Integer.parseInt(time.split("-")[2]);
            int h = Integer.parseInt(time.split("-")[3].split(":")[0]);
            int mm = Integer.parseInt(time.split("-")[3].split(":")[1]);
            Calendar calendar = Calendar.getInstance();
            int y2 = calendar.get(Calendar.YEAR);
            int m2 = calendar.get(Calendar.MONTH) + 1;
            int d2 = calendar.get(Calendar.DAY_OF_MONTH);
            int h2 = calendar.get(Calendar.HOUR_OF_DAY);
            int mm2 = calendar.get(Calendar.MINUTE);
            if (y > y2) return false;
            if (y == y2 && m > m2) return false;
            if (y == y2 && m == m2 && d > d2) return false;
            if (y == y2 && m == m2 && d == d2 && h > h2) return false;
            if (y == y2 && m == m2 && d == d2 && h == h2 && mm > mm2) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void checkLessons(String studentCode, MySQLHandler handler) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        UnitSelectionDataHandler dataHandler = new UnitSelectionDataHandler(handler);
        List<String> lessons = dataHandler.getStudentLessons(studentCode, true);
        List<String> lastTermLessons = new ArrayList<>();
        List<String> thisTermLessons = new ArrayList<>();
        List<String> removedLessonCodes = new ArrayList<>();
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            if (term.equals(thisTerm)) thisTermLessons.add(lessonCode);
            else lastTermLessons.add(lessonCode);
        }
        for (String lesson : thisTermLessons) {
            List<String> prerequisites = dataHandler.getPrerequisites(lesson);
            for (String prerequisite : prerequisites) {
                if (!lastTermLessons.contains(prerequisite)) {
                    removedLessonCodes.add(lesson);
                    break;
                }
            }
        }
        for (String lesson : thisTermLessons) {
            List<String> theNeeds = dataHandler.getNeeds(lesson);
            for (String need : theNeeds) {
                if (!thisTermLessons.contains(need)) {
                    removedLessonCodes.add(lesson);
                    break;
                }
            }
        }
        List<String> removedLessons = new ArrayList<>();
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            if (!term.equals(thisTerm)) continue;
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            if (removedLessonCodes.contains(lessonCode)) removedLessons.add(lesson);
        }
        lessons.removeAll(removedLessons);
        dataHandler.updateStudentLessons(studentCode, lessons);
    }

    private static void makeCourses(String collegeCode, MySQLHandler handler) {
        CoursesManager manager = new CoursesManager(handler);
        manager.createAllCollegeLessons(collegeCode);
    }

    private Response sendOKResponse() {
        Response response = new Response(ResponseStatus.OK);
        String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
        response.setNotificationMessage(note);
        return response;
    }

    private Response sendErrorResponse() {
        Response response = new Response(ResponseStatus.ERROR);
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        response.setErrorMessage(error);
        return response;
    }
}
