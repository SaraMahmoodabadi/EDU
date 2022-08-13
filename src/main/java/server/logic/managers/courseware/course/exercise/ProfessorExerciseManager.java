package server.logic.managers.courseware.course.exercise;

import server.database.dataHandlers.courseware.course.exercise.ProfessorExerciseDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Answer;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.util.ArrayList;

public class ProfessorExerciseManager {
    private final ProfessorExerciseDataHandler dataHandler;

    public ProfessorExerciseManager(ClientHandler clientHandler) {
        this.dataHandler = new ProfessorExerciseDataHandler(clientHandler.getDataHandler());
    }

    public Response getAnswers(Request request) {
        String exerciseCode = (String) request.getData("exerciseCode");
        String name = this.dataHandler.getExerciseName(exerciseCode);
        ArrayList<Answer> answers = this.dataHandler.getAnswers(exerciseCode);
        Response response = new Response(ResponseStatus.OK);
        response.addData("name", name);
        MediaHandler handler = new MediaHandler();
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            if (answer.getAnswerType() == ItemType.MEDIA_FILE)
                answer.setFileAddress(handler.encode(answer.getFileAddress()));
            response.addData("answer" + i, answer);
        }
        return response;
    }

    public Response registerScore(Request request) {
        String exerciseCode = (String) request.getData("exerciseCode");
        String studentCode = (String) request.getData("studentCode");
        String score = (String) request.getData("score");
        boolean result = this.dataHandler.registerScore(exerciseCode, studentCode, score);
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            Response response = new Response(ResponseStatus.ERROR);
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            response.setErrorMessage(error);
            return response;
        }
    }
}
