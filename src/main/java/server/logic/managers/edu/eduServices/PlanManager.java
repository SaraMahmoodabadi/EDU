package server.logic.managers.edu.eduServices;

import server.database.MySQLHandler;
import server.database.dataHandlers.edu.MainDataHandler;
import server.database.dataHandlers.edu.eduServises.PlanDataHandler;
import server.database.dataHandlers.edu.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.model.user.UserType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.*;

public class PlanManager {
    private final PlanDataHandler dataHandler;
    private final ClientHandler client;

    public PlanManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new PlanDataHandler(clientHandler.getDataHandler());
    }

    public Response getWeeklyPlan() {
        List<String> lessonsCode = getThisTermLessons(this.dataHandler.getUserWeeklyPlan
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName()));
        List<Lesson> lessons = this.dataHandler.getLessonsWeeklyPlan(lessonsCode);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < lessons.size() ; i++) {
            response.addData("lesson" + i, lessons.get(i));
        }
        return response;
    }

    public Response getExamList() {
        List<String> lessonsCode = getThisTermLessons(this.dataHandler.getUserExams
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName()));
        List<Lesson> lessons = this.dataHandler.getLessonsExam(lessonsCode);
        HashMap<Lesson, Double> examTime = new HashMap<>();
        for (Lesson lesson : lessons) {
            String exam = lesson.getExamTime();
            String time = exam.split("-")[3];
            double date = Integer.parseInt(exam.split("-")[0]) * 365 +
                    Integer.parseInt(exam.split("-")[1]) * 12 +
                    Integer.parseInt(exam.split("-")[2]) +
                    Integer.parseInt(time.split(":")[0]) / 24.0 +
                    Integer.parseInt(time.split(":")[1]) / (24.0 * 60.0);
            examTime.put(lesson, date);
        }
        List<Double> times = new ArrayList<>(examTime.values());
        Collections.sort(times);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < times.size(); i++) {
            for (Map.Entry<Lesson, Double> entry : examTime.entrySet()) {
                if (Objects.equals(entry.getValue(), times.get(i))) {
                    response.addData("lesson" + i, entry.getKey());
                }
            }
        }
        return response;
    }

    private List<String> getThisTermLessons(List<String> lessons) {
        String thisTerm;
        if (registrationPassed()) {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        }
        else {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "lastTerm");
        }
        List<String> newList = new ArrayList<>();
        if (lessons != null) {
            for (String lesson : lessons) {
                String term = lesson.split("-")[0];
                if (!term.equals(thisTerm)) continue;
                String lessonCode = getMainLessonCode(lesson);
                newList.add(lessonCode);
            }
        }
        return newList;
    }

    private boolean registrationPassed() {
        if (isPassed()) return true;
        String time = getRegistrationTime();
        if (client.getUserType() == UserType.STUDENT) {
            if (time == null) return true;//todo: ?
        } else if (client.getUserType() == UserType.PROFESSOR) {
            return false;
        }
        try {
            int y = Integer.parseInt(time.split("-")[0]);
            int m = Integer.parseInt(time.split("-")[1]);
            int d = Integer.parseInt(time.split("-")[2]);
            int h = Integer.parseInt(time.split("-")[4].split(":")[0]);
            int mm = Integer.parseInt(time.split("-")[4].split(":")[1]);
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

    private String getRegistrationTime() {
        if (client.getUserType() == UserType.STUDENT) {
            MainDataHandler dataHandler = new MainDataHandler(this.client.getDataHandler());
            return dataHandler.getUnitSelectionTime(this.client.getUserName());
        }
        else return null;
    }

    private boolean isPassed() {
        String time = new UnitSelectionDataHandler(new MySQLHandler())
                .getCollegeRegistrationTime(this.dataHandler.getCollegeCode(this.client.getUserName()));
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

    private String getMainLessonCode(String lesson) {
        String term = lesson.split("-")[0];
        int n = lesson.split("-").length;
        String group = lesson.split("-")[n - 1];
        return lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
    }
}
