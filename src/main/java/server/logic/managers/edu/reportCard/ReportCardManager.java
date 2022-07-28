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

public class ReportCardManager {
    private final ClientHandler client;
    private final TemporaryScoresDataHandler dataHandler;

    public ReportCardManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new TemporaryScoresDataHandler(clientHandler.getDataHandler());
    }

    public Response getStudentTemporaryScores(Request request) {
        String studentCode = "";
        if (this.client.getUserType() == UserType.PROFESSOR) {
            if (request.getData("collegeCode").equals
                    (this.dataHandler.getStudentCollege((String) request.getData("studentCode")))) {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
                return getErrorResponse(errorMessage);
            }
            studentCode = (String) request.getData("studentCode");
        }
        List<Score> scores = this.dataHandler.getStudentScores(this.client.getUserName(),
                this.client.getUserType(), studentCode);
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
            return getLessonSummary(scores, response);
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
        List<String> studentsCode = new ArrayList<>();
        boolean result = true;
        for (Map.Entry<String,Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("score")) {
                studentsCode.add(((Score) entry.getValue()).getStudentCode());
                if (((Score) entry.getValue()).getScore() == null) break;
                result = this.dataHandler.finalizeScores((String) request.getData("score"),
                        ((Score) entry.getValue()).getLessonCode(), this.client.getUserName());
                if (!result) break;
            }
        }
        if (result) {
            calculateRate(studentsCode);
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

    private synchronized void calculateRate(List<String> students) {
        for (String student : students) {
            Map<Score, Integer> scores = this.dataHandler.getFinalScores(student);
            int unitSum = 0;
            double scoreSum = 0.0;
            for (Map.Entry<Score,Integer> entry : scores.entrySet()) {
                scoreSum += Double.parseDouble(entry.getKey().getScore());
                unitSum += entry.getValue();
            }
            double rate = scoreSum / unitSum;
            this.dataHandler.registerRate(rate, student);
        }
    }

    public Response getProfessorScores(Request request) {
        List<Score> scores = this.dataHandler.getProfessorScores((String) request.getData("professorName"),
               (String) request.getData("collegeCode"));
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

    public Response getLessonSummary(List<Score> scores, Response response) {
        double sum = 0.0;
        double sumPassed = 0.0;
        int numberPassed = 0;
        for (Score score : scores) {
            sum += Double.parseDouble(score.getScore());
            if (Double.parseDouble(score.getScore()) >= 10) {
                sumPassed += Double.parseDouble(score.getScore());
                numberPassed++;
            }
        }
        double average = sum / scores.size();
        double averagePassed = sumPassed / numberPassed;
        response.addData("average", average);
        response.addData("averagePassed", averagePassed);
        response.addData("numberPassed", numberPassed);
        response.addData("numberFailed", scores.size() - numberPassed);
        return response;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
