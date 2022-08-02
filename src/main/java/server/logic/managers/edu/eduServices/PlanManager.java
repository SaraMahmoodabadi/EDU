package server.logic.managers.edu.eduServices;

import server.database.MySQLHandler;
import server.database.dataHandlers.MainDataHandler;
import server.database.dataHandlers.eduServises.PlanDataHandler;
import server.database.dataHandlers.unitSelection.UnitSelectionDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDate;
import java.util.*;

public class PlanManager {
    private final PlanDataHandler dataHandler;
    private final ClientHandler client;

    public PlanManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new PlanDataHandler(clientHandler.getDataHandler());
    }

    public Response getWeeklyPlan() {
        List<Lesson> lessons = getThisTermLessons(this.dataHandler.getUserWeeklyPlan
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName()));
        Response response = new Response(ResponseStatus.OK);
        for (int i = 1; i <= lessons.size() ; i++) {
            response.addData("lesson" + i, lessons.get(i));
        }
        return response;
    }

    public Response getExamList() {
        List<Lesson> lessons = getThisTermLessons(this.dataHandler.getUserExams
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName()));
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
        for (double time : times) {
            for (Map.Entry<Lesson, Double> entry : examTime.entrySet()) {
                if (Objects.equals(entry.getValue(), time)) {
                    response.addData("lesson" +
                            entry.getKey().getLessonCode(), entry.getKey());
                }
            }
        }
        return response;
    }

    private List<Lesson> getThisTermLessons(List<Lesson> lessons) {
        String thisTerm;
        if (registrationPassed()) {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        }
        else {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "lastTerm");
        }
        List<Lesson> newList = new ArrayList<>();
        if (lessons != null) {
            for (Lesson lesson : lessons) {
                String term = lesson.getLessonCode().split("-")[0];
                if (!term.equals(thisTerm)) continue;
                String lessonCode = getMainLessonCode(lesson.getLessonCode());
                lesson.setLessonCode(lessonCode);
                newList.add(lesson);
            }
        }
        return newList;
    }

    private boolean registrationPassed() {
        if (isPassed()) return true;
        String time = getRegistrationTime();
        String now = String.valueOf(LocalDate.now());
        int t1 = Integer.parseInt(time.split("-")[0]) * 365 +
                Integer.parseInt(time.split("-")[1]) * 12 +
                Integer.parseInt(time.split("-")[2]);
        int t2 = Integer.parseInt(now.split("-")[0]) * 365 +
                Integer.parseInt(now.split("-")[1]) * 12 +
                Integer.parseInt(now.split("-")[2]);
        return t2 > t1;
    }

    private String getRegistrationTime() {
        MainDataHandler dataHandler = new MainDataHandler(this.client.getDataHandler());
        return dataHandler.getUnitSelectionTime(this.client.getUserName());
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
            int m2 = calendar.get(Calendar.MONTH);
            int d2 = calendar.get(Calendar.DAY_OF_MONTH);
            int h2 = calendar.get(Calendar.HOUR_OF_DAY);
            int mm2 = calendar.get(Calendar.MINUTE);
            if (y > y2) return false;
            if (y == y2 && m > m2) return false;
            if (y == y2 && m == m2 && d > d2) return false;
            return y != y2 || m != m2 || d != d2 || h <= h2;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMainLessonCode(String lesson) {
        String term = lesson.split("-")[0];
        int n = lesson.split("-").length;
        String group = lesson.split("-")[n - 1];
        return lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
    }
}
