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
import java.util.List;

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
            return new Response(ResponseStatus.OK);
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

    //TODO
    public List<Score> getFinalScores() {
        return null;
    }


    //TODO
    public void setProtestAnswer() {

    }

    //TODO
    public void setScore() {

    }

    //TODO
    public void finalizeScores() {

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
