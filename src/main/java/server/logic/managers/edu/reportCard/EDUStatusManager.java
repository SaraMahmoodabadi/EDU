package server.logic.managers.edu.reportCard;

import server.database.dataHandlers.edu.score.EDUStatusDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.score.Score;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.*;

public class EDUStatusManager {
    private final ClientHandler client;
    private final EDUStatusDataHandler dataHandler;

    public EDUStatusManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new EDUStatusDataHandler(clientHandler.getDataHandler());
    }

    public Response getScores(Request request) {
        String studentCode;
        if (this.client.getUserType() == UserType.PROFESSOR) {
            String collegeCode = (String) request.getData("collegeCode");
            if (request.getData("studentCode") != null) {
                studentCode = (String) request.getData("studentCode");
            }
            else {
                String studentName = (String) request.getData("studentName");
                studentCode = this.dataHandler.getStudentCode(studentName, collegeCode);
                if (studentCode == null) {
                    String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                            (String.class, "invalidInputs");
                    return getErrorResponse(errorMessage);
                }
            }
            if (this.dataHandler.getCollegeCode(studentCode) == null) {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                        (String.class, "invalidInputs");
                return getErrorResponse(errorMessage);
            }
            if (!this.dataHandler.getCollegeCode(studentCode).equals(collegeCode)) {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                        (String.class, "invalidInputs");
                return getErrorResponse(errorMessage);
            }
        }
        else {
            studentCode = this.dataHandler.getStudentCode(this.client.getUserName());
        }
        return getAllScores(studentCode);
    }

    public Response getAllScores(String studentCode) {
        Map<Score, Integer> scores = this.dataHandler.getFinalScores(studentCode);
        List<String> lessons = getMainLessonCodes(this.dataHandler.getLessons(studentCode));
        List<String> finalScores = new ArrayList<>();
        for (Score score : scores.keySet()) {
            finalScores.add(score.getLessonCode());
        }
        List<Score> scoreList = new ArrayList<>(scores.keySet());
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < scoreList.size(); i++) {
            response.addData("score" + i, scoreList.get(i));
        }
        for (int i = 0; i < lessons.size(); i++) {
            if (!finalScores.contains(lessons.get(i))) {
                Score score = new Score(lessons.get(i), studentCode, "N");
                response.addData("score" + (i + scoreList.size()), score);
            }
        }
        int passed = 0;
        for (Map.Entry<Score, Integer> entry : scores.entrySet()) {
            if (Double.parseDouble(entry.getKey().getScore()) >= 10.0)
                passed += entry.getValue();
        }
        response.addData("average", this.dataHandler.getRate(studentCode));
        response.addData("numberPassed", passed);
        return response;
    }

    private List<String> getMainLessonCodes(List<String> lessons) {
        List<String> newList = new ArrayList<>();
        for (String lesson : lessons) {
            String term = lesson.split("-")[0];
            int n = lesson.split("-").length;
            String group = lesson.split("-")[n - 1];
            String lessonCode = lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
            newList.add(lessonCode);
        }
        return newList;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
