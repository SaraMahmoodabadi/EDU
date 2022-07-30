package server.logic.managers.edu.unitSelection;

import server.database.dataHandlers.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.model.university.lesson.score.Score;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.*;
import java.util.stream.Collectors;

public class UnitSelectionManager {
    private final UnitSelectionDataHandler dataHandler;
    private final ClientHandler client;

    public UnitSelectionManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new UnitSelectionDataHandler(clientHandler.getDataHandler());
    }

    public Response getCollegeLesson(Request request) {
        String collegeName = (String) request.getData("college");
        String sort = (String) request.getData("sort");
        List<Lesson> lessons = this.dataHandler.getCollegeLesson(collegeName);
        List<Lesson> sortedList = new ArrayList<>();
        switch (sort) {
            case "alphabetic" : sortedList = sortByAlphabet(lessons);
                break;
            case "examTime" : sortedList = sortByExamTime(lessons);
                break;
            case "grade" : sortedList = sortByGrade(lessons);
                break;
        }
        return sendLessons(sortedList);
    }

    public Response getSuggestedLessons() {
        Grade grade = this.dataHandler.getStudentGrade(this.client.getUserName());
        String items = " l.grade = '" + grade + "'";
        List<Lesson> lessons = this.dataHandler.getSuggestedLessons(items);
        List<String> markedLessons = this.dataHandler.getStudentMarkedLessons(this.client.getUserName());
        for (String lesson : markedLessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            Lesson markedLesson = this.dataHandler.getLesson(lessonCode, Integer.parseInt(group));
            if (!lessons.contains(markedLesson)) lessons.add(markedLesson);
        }
        List<Lesson> newList = removeFullLessons(lessons);
        List<Lesson> finalList = compareToScore(newList);
        return sendLessons(finalList);
    }

    private Response sendLessons(List<Lesson> lessons) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        List<String> markedLessons = this.dataHandler.getStudentMarkedLessons(client.getUserName());
        List<String> tookLessons = this.dataHandler.getStudentLessons(client.getUserName());
        Response response = new Response(ResponseStatus.OK);
        for (Lesson lesson : lessons) {
            if (markedLessons.contains(thisTerm + "-" + lesson.getLessonCode() + "-" + lesson.getGroup())) {
                if (tookLessons.contains(thisTerm + "-" + lesson.getLessonCode() + "-" + lesson.getGroup())) {
                    response.addData("lessonRM", lesson);
                }
                else
                    response.addData("lessonNM", lesson);
            }
            else {
                if (tookLessons.contains(thisTerm + "-" + lesson.getLessonCode() + "-" + lesson.getGroup())) {
                    response.addData("lessonRN", lesson);
                }
                else
                    response.addData("lessonNN", lesson);
            }
        }
        return response;
    }

    private List<Lesson> sortByAlphabet(List<Lesson> lessons) {
        List<String> names = new ArrayList<>();
        Map<String, Lesson> lessonMap = new HashMap<>();
        for (Lesson lesson : lessons) {
            String name = lesson.getName() + lesson.getGroup();
            lessonMap.put(name, lesson);
            names.add(name);
        }
        Collections.sort(names);
        List<Lesson> newList = new ArrayList<>();
        for (String name : names) {
            newList.add(lessonMap.get(name));
        }
        return newList;
    }

    private List<Lesson> sortByExamTime(List<Lesson> lessons) {
        List<Double> times = new ArrayList<>();
        Map<Double, String> lessonMap = new HashMap<>();
        for (Lesson lesson : lessons) {
            String examTime = lesson.getExamTime();
            double time = Integer.parseInt(examTime.split("-")[0]) * 365 +
                    Integer.parseInt(examTime.split("-")[1]) * 12 +
                    Integer.parseInt(examTime.split("-")[2]) +
                    Integer.parseInt(examTime.split("-")[3].split(":")[0]) / 24.0 +
                    Integer.parseInt(examTime.split("-")[3].split(":")[1]) / 60.0;
            lessonMap.put(time, examTime);
            times.add(time);
        }
        Collections.sort(times);
        List<Lesson> newList = new ArrayList<>();
        for (Double time : times) {
            String examTime = lessonMap.get(time);
            for (Lesson lesson : lessons) {
                if (lesson.getExamTime().equals(examTime))
                    newList.add(lesson);
            }
        }
        return newList;
    }

    private List<Lesson> sortByGrade(List<Lesson> lessons) {
        List<Lesson> lessons1 = lessons.stream().filter
                ((V) -> V.getGrade() == Grade.UNDERGRADUATE).collect(Collectors.toList());
        List<Lesson> lessons2 = lessons.stream().filter
                ((V) -> V.getGrade() == Grade.MASTER).collect(Collectors.toList());
        List<Lesson> lessons3 = lessons.stream().filter
                ((V) -> V.getGrade() == Grade.PHD).collect(Collectors.toList());
        List<Lesson> newList = new ArrayList<>();
        newList.addAll(lessons1);
        newList.addAll(lessons2);
        newList.addAll(lessons3);
        return newList;
    }

    private List<Lesson> removeFullLessons(List<Lesson> lessons) {
        List<Lesson> removedLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            List<String> students = this.dataHandler.getGroupStudents(lesson.getLessonCode(),
                    String.valueOf(lesson.getGroup()));
            int capacity = this.dataHandler.getGroupCapacity(lesson.getLessonCode(),
                    String.valueOf(lesson.getGroup()));
            if (students.size() <= capacity) removedLessons.add(lesson);
        }
        lessons.removeAll(removedLessons);
        return lessons;
    }

    private List<Lesson> compareToScore(List<Lesson> lessons) {
        List<Score> scores = this.dataHandler.getStudentScores(this.client.getUserName());
        List<String> passedLessons = new ArrayList<>();
        for (Score score : scores) {
            if (Double.parseDouble(score.getScore()) >= 10.0) {
                passedLessons.add(score.getLessonCode());
            }
        }
        List<Lesson> removedLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            List<String> prerequisites = this.dataHandler.getPrerequisites(lesson.getLessonCode());
            if (prerequisites == null) prerequisites = new ArrayList<>();
            for (String lessonCode : prerequisites) {
                if (!passedLessons.contains(lessonCode)) {
                    removedLessons.add(lesson);
                    break;
                }
            }
        }
        lessons.removeAll(removedLessons);
        return lessons;
    }

    public Response markLesson(Request request) {
       boolean result = this.dataHandler.markLesson(getLessonCodeFormat(request), this.client.getUserName());
       if (result) {
           String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
           Response response = new Response(ResponseStatus.OK);
           response.setNotificationMessage(note);
           return response;
       }
       else {
           String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
           return sendErrorResponse(error);
       }
    }

    public Response unmarkedLesson(Request request) {
        boolean result = this.dataHandler.unmarkedLesson(getLessonCodeFormat(request), this.client.getUserName());
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    public Response takeLesson(Request request) {
        if (checkLessonIsTaken(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "lessonTakenError");
            return sendErrorResponse(error);
        }
        if (checkLessonCapacity(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "lessonCapacityError");
            return sendErrorResponse(error);
        }
        if (checkLessonPrerequisites(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "prerequisiteError");
            return sendErrorResponse(error);
        }
        if (checkClassTime(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "classTimeError");
            return sendErrorResponse(error);
        }
        if (checkExamTime(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "examTimeError");
            return sendErrorResponse(error);
        }
        if (checkReligiousCourses(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "religiousCourseError");
            return sendErrorResponse(error);
        }
        boolean result = this.dataHandler.takeLesson(getLessonCodeFormat(request), this.client.getUserName());
        if (result) {
            this.dataHandler.addStudentToGroup((String) request.getData("lessonCode"),
                    (String) request.getData("group"), this.client.getUserName());
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private boolean checkLessonPrerequisites(Request request) {
        List<Score> scores = this.dataHandler.getStudentScores(this.client.getUserName());
        List<String> passedLessons = new ArrayList<>();
        for (Score score : scores) {
            if (Double.parseDouble(score.getScore()) >= 10.0) {
                passedLessons.add(score.getLessonCode());
            }
        }
        List<String> prerequisites = this.dataHandler.getPrerequisites((String) request.getData("lessonCode"));
        if (prerequisites == null) prerequisites = new ArrayList<>();
        for (String lessonCode : prerequisites) {
            if (!passedLessons.contains(lessonCode)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLessonCapacity(Request request) {
        String lessonCode = (String) request.getData("lessonCode");
        String group = (String) request.getData("group");
        List<String> students = this.dataHandler.getGroupStudents(lessonCode, group);
        int capacity = this.dataHandler.getGroupCapacity(lessonCode, group);
        return students.size() <= capacity;
    }

    private boolean checkClassTime(Request request) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String thisLessonCode = (String) request.getData("lessonCode");
        String thisDays = this.dataHandler.getLessonData("days", thisLessonCode);
        String thisTime = this.dataHandler.getLessonData("classTime", thisLessonCode);
        String thisDaysArray = thisDays.substring(1, thisDays.length() - 1);
        List<String> thisDaysList = new ArrayList<>(Arrays.asList(thisDaysArray.split(",")));
        List<String> lessons = this.dataHandler.getStudentLessons(this.client.getUserName());
        for (String lesson : lessons) {
            int n = lesson.split("-").length;
            String term = lesson.split("-")[0];
            if (!term.equals(thisTerm)) continue;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            String days = this.dataHandler.getLessonData("days", lessonCode);
            String time = this.dataHandler.getLessonData("classTime", lessonCode);
            if (checkTime(thisTime, time)) continue;
            String daysArray = thisDays.substring(1, days.length() - 1);
            List<String> daysList = new ArrayList<>(Arrays.asList(daysArray.split(",")));
            for (String day : daysList) {
                if (thisDaysList.contains(day)) return true;
            }
        }
        return false;
    }

    private boolean checkTime(String time1, String time2) {
        int h1 = Integer.parseInt(time1.split("-")[0].split(":")[0]);
        int m1 = Integer.parseInt(time1.split("-")[0].split(":")[1]);
        int h2 = Integer.parseInt(time1.split("-")[1].split(":")[0]);
        int m2 = Integer.parseInt(time1.split("-")[1].split(":")[1]);
        int h3 = Integer.parseInt(time2.split("-")[0].split(":")[0]);
        int m3 = Integer.parseInt(time2.split("-")[0].split(":")[1]);
        int h4 = Integer.parseInt(time2.split("-")[1].split(":")[0]);
        int m4 = Integer.parseInt(time2.split("-")[1].split(":")[1]);
        int start1 = h1 * 60 + m1;
        int end1 = h2 * 60 + m2;
        int start2 = h3 * 60 + m3;
        int end2 = h4 * 60 + m4;
        if (start2 > start1 && end1 > start2) return false;
        return start1 <= start2 || end2 <= start1;
    }

    private boolean checkExamTime(Request request) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String thisLessonCode = (String) request.getData("lessonCode");
        String thisExamTime = this.dataHandler.getLessonData("examTime", thisLessonCode);
        int y1 = Integer.parseInt(thisExamTime.split("-")[0]);
        int m1 = Integer.parseInt(thisExamTime.split("-")[1]);
        int d1 = Integer.parseInt(thisExamTime.split("-")[2]);
        int h1 = Integer.parseInt(thisExamTime.split("-")[3]);
        int mm1 = Integer.parseInt(thisExamTime.split("-")[4]);
        List<String> lessons = this.dataHandler.getStudentLessons(this.client.getUserName());
        for (String lesson : lessons) {
            int n = lesson.split("-").length;
            String term = lesson.split("-")[0];
            if (!term.equals(thisTerm)) continue;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            String examTime = this.dataHandler.getLessonData("examTime", lessonCode);
            int y2 = Integer.parseInt(thisExamTime.split("-")[0]);
            int m2 = Integer.parseInt(thisExamTime.split("-")[1]);
            int d2 = Integer.parseInt(thisExamTime.split("-")[2]);
            int h2 = Integer.parseInt(thisExamTime.split("-")[3]);
            int mm2 = Integer.parseInt(thisExamTime.split("-")[4]);
            if (y1 == y2 && m1 == m2 && d1 == d2) {
                int start1 = h1 * 60 + mm1;
                int start2 = h2 * 69 + mm2;
                if (Math.abs(start1 - start2) < 60) return true;
            }
        }
        return false;
    }

    private boolean checkReligiousCourses(Request request) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String religiousCollege = Config.getConfig(ConfigType.GUI_TEXT).getProperty
                (String.class, "religiousCollegeCode");
        String thisLesson = (String) request.getData("lessonCode");
        String thisCollegeCode = this.dataHandler.getLessonData("collegeCode", thisLesson);
        if (!thisCollegeCode.equals(religiousCollege)) return false;
        List<String> lessons = this.dataHandler.getStudentLessons(this.client.getUserName());
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            if (!term.equals(thisTerm)) continue;
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            String collegeCode = this.dataHandler.getLessonData("collegeCode", lessonCode);
            if (collegeCode.equals(religiousCollege)) return true;
        }
        return false;
    }

    private boolean checkLessonIsTaken(Request request) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String thisLesson = (String) request.getData("lessonCode");
        List<String> lessons = this.dataHandler.getStudentLessons(this.client.getUserName());
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            if (thisLesson.equals(term) && lessonCode.equals(lesson)) return true;
            if (lessonCode.equals(lesson)) {
                List<Score> scores = this.dataHandler.getStudentScores(this.client.getUserName());
                for (Score score : scores) {
                    if (score.getLessonCode().equals(lesson) &&
                    Double.parseDouble(score.getScore()) >= 10) return true;
                }
            }
        }
        return false;
    }

    public Response changeLessonGroup(Request request) {
        if (checkLessonCapacity(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "lessonCapacityError");
            return sendErrorResponse(error);
        }
        if (checkLessonPrerequisites(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "prerequisiteError");
            return sendErrorResponse(error);
        }
        if (checkClassTime(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "classTimeError");
            return sendErrorResponse(error);
        }
        if (checkExamTime(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "examTimeError");
            return sendErrorResponse(error);
        }
        if (checkReligiousCourses(request)) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "religiousCourseError");
            return sendErrorResponse(error);
        }
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String lessonCode = (String) request.getData("lessonCode");
        String group = findGroup(lessonCode);
        boolean result = this.dataHandler.takeLesson(getLessonCodeFormat(request), this.client.getUserName());
        if (result) {
            this.dataHandler.removeStudentFromGroup(lessonCode,
                    (String) request.getData("group"), this.client.getUserName());
            this.dataHandler.addStudentToGroup(lessonCode,
                    (String) request.getData("group"), this.client.getUserName());
            this.dataHandler.removeLesson(thisTerm + "-" + lessonCode + "-" + group, this.client.getUserName());
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private String findGroup(String lessonCode) {
        List<String> lessons = this.dataHandler.getStudentLessons(this.client.getUserName());
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String thisLessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            if (thisLessonCode.equals(lessonCode)) return group;
        }
        return null;
    }

    public Response requestToTakeLesson(Request request) {
        boolean result = this.dataHandler.requestGetLesson(this.client.getUserName(), getLessonCodeFormat(request));
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            sendErrorResponse(error);
        }
        return null;
    }

    public Response removeLesson(Request request) {
        boolean result = this.dataHandler.removeLesson(getLessonCodeFormat(request), this.client.getUserName());
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            sendErrorResponse(error);
        }
        return null;
    }

    private String getLessonCodeFormat(Request request) {
        String thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        String lessonCode = (String) request.getData("lessonCode");
        String group = (String) request.getData("group");
        return thisTerm + "-" + lessonCode + "-" + group;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
