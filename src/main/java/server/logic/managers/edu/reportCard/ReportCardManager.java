package server.logic.managers.edu.reportCard;

import server.database.dataHandlers.score.TemporaryScoresDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.score.Score;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReportCardManager {
    private final ClientHandler client;
    private final TemporaryScoresDataHandler dataHandler;

    public ReportCardManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new TemporaryScoresDataHandler(clientHandler.getDataHandler());
    }

    public Response getStudentTemporaryScores(Request request) {
        List<Score> scores = new ArrayList<>();
        if (this.client.getUserType() == UserType.STUDENT) {
            scores = this.dataHandler.getStudentScores(this.client.getUserName());
        }
        if (scores != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < scores.size(); i++) {
                response.addData("score" + i, scores.get(i));
            }
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
        return getErrorResponse(errorMessage);
    }

    public Response setProtest(Request request) {
        Score score = (Score) request.getData("score");
        boolean result = this.dataHandler.setProtest(
                (String) request.getData("protest"), score.getLessonCode(),
                this.client.getUserName());
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
            return getErrorResponse(errorMessage);
        }
    }

    public Response getLessonTemporaryScores(Request request) {
        List<Score> scores = this.dataHandler.getLessonScores
                ((String) request.getData("lessonCode"), this.client.getUserName());
        if (scores != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < scores.size(); i++) {
                response.addData("score" + i, scores.get(i));
            }
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response setProtestAnswer(Request request) {
        Score score = (Score) request.getData("score");
        boolean result = this.dataHandler.setProtestAnswer(
                (String) request.getData("protestAnswer"), score.getLessonCode(),
                score.getStudentCode());
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
            return getErrorResponse(errorMessage);
        }
    }

    public Response setScore(Request request) {
        Score score = (Score) request.getData("score");
        boolean result = this.dataHandler.setScore(
                (String) request.getData("score"), score.getLessonCode(),
                score.getStudentCode());
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
            return getErrorResponse(errorMessage);
        }
    }

    public Response setScores(Request request) {
        HashMap<String, Object> data = request.getData();
        boolean result = true;
        for (Map.Entry<String,Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("score")) {
                if (((Score) entry.getValue()).getScore() == null) break;
                result = this.dataHandler.setScore((String) request.getData("score"),
                        ((Score) entry.getValue()).getLessonCode(), ((Score) entry.getValue()).getStudentCode());
                if (!result) break;
            }
        }
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
            return getErrorResponse(errorMessage);
        }
    }

    public Response finalizeScores(Request request) {
        HashMap<String, Object> data = request.getData();
        boolean result = true;
        for (Map.Entry<String,Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("score")) {
                if (((Score) entry.getValue()).getScore() == null) break;
                result = this.dataHandler.finalizeScores((String) request.getData("score"),
                        ((Score) entry.getValue()).getLessonCode(), this.client.getUserName());
                if (!result) break;
            }
        }
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
            return getErrorResponse(errorMessage);
        }
    }

    //TODO
    public List<String> getLessonSummary() {
        return null;
    }

    //TODO
    public int getPassedLessonsNumber() {
        return 0;
    }

    //TODO
    public double getRate() {
        return 0;
    }

    //TODO
    public void getInformation() {

    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
