package server.logic.managers.edu.eduServices;

import server.database.dataHandlers.PlanDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
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
        List<Lesson> lessons = this.dataHandler.getUserWeeklyPlan
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName());
        if (lessons != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 1; i <= lessons.size() ; i++) {
                response.addData("lesson" + i, lessons.get(i));
            }
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
        return getErrorResponse(errorMessage);
    }

    public Response getExamList() {
        List<Lesson> lessons = this.dataHandler.getUserExams
                (String.valueOf(this.client.getUserType()).toLowerCase(), this.client.getUserName());
        if (lessons != null) {
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
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
        return getErrorResponse(errorMessage);
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
