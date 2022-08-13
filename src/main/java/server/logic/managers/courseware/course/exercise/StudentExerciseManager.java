package server.logic.managers.courseware.course.exercise;

import server.database.dataHandlers.courseware.course.exercise.StudentExerciseDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Exercise;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

public class StudentExerciseManager {
    private final ClientHandler client;
    private final StudentExerciseDataHandler dataHandler;

    public StudentExerciseManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new StudentExerciseDataHandler(clientHandler.getDataHandler());
    }

    public Response getExercise(Request request) {
        String exerciseCode = (String) request.getData("exerciseCode");
        Exercise exercise = this.dataHandler.getExercise(exerciseCode);
        if (exercise == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
        MediaHandler handler = new MediaHandler();
        if (exercise.getItemType() == ItemType.MEDIA_FILE)
            exercise.setFileAddress(handler.encode(exercise.getFileAddress()));
        boolean hasSubmitted = this.dataHandler.hasSubmitted(exerciseCode, this.client.getUserName());
        String submissionStatus;
        String score;
        if (hasSubmitted) {
            submissionStatus = "submitted";
            score = this.dataHandler.getScore(exerciseCode, this.client.getUserName());
        }
        else {
            submissionStatus = "not submitted";
            score = "-";
        }
        Response response = new Response(ResponseStatus.OK);
        response.addData("exercise", exercise);
        response.addData("submissionStatus", submissionStatus);
        response.addData("score", score);
        return response;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
