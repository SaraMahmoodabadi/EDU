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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

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

    public Response getAnswer(Request request) {
        String exerciseCode = (String) request.getData("exerciseCode");
        ItemType type = (ItemType) request.getData("type");
        String answer = (String) request.getData("answer");
        String checkTime = checkUploadTime(exerciseCode);
        if (checkTime == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
            return sendErrorResponse(error);
        }
        else if (!checkTime.equals("OK")) return sendErrorResponse(checkTime);
        boolean hasSubmitted = this.dataHandler.hasSubmitted(exerciseCode, this.client.getUserName());
        boolean result;
        if (type == ItemType.MEDIA_FILE) {
            String fileFormat = (String) request.getData("fileFormat");
            String path = saveFile(answer, fileFormat);
            if (hasSubmitted) {
                String lastFile = this.dataHandler.getFileAddress(exerciseCode, this.client.getUserName());
                result = this.dataHandler.updateMediaAnswer(exerciseCode, this.client.getUserName(), path);
                if (result) deleteFile(lastFile);
            }
            else
                result = this.dataHandler.saveMediaAnswer(exerciseCode, this.client.getUserName(), type, path);
        }
        else {
            if (hasSubmitted)
                result = this.dataHandler.updateTextAnswer(exerciseCode, this.client.getUserName(), answer);
            else
                result = this.dataHandler.saveTextAnswer(exerciseCode, this.client.getUserName(), type, answer);
        }
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    private String checkUploadTime(String exerciseCode) {
        String openingTime = this.dataHandler.getOpeningTime(exerciseCode);
        String closingTime = this.dataHandler.getClosingTime(exerciseCode);
        if (openingTime == null || closingTime == null) {
            return Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        }
        String openingTimeMessage = checkOpeningTime(openingTime);
        if (!openingTimeMessage.equals("OK")) return openingTimeMessage;
        String closingTimeMessage = checkClosingTime(closingTime);
        if (!closingTimeMessage.equals("OK")) return closingTimeMessage;
        return "OK";
    }

    private String checkOpeningTime(String openingTime) {
        LocalDateTime now = LocalDateTime.now();
        String time = openingTime.substring(0, 10) + "T" + openingTime.substring(11, 16) + ":00.000";
        LocalDateTime openTime = LocalDateTime.parse(time);
        if (openTime.isBefore(now))
            return Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "exerciseOpeningTime");
        return "OK";
    }

    private String checkClosingTime(String closingTime) {
        LocalDateTime now = LocalDateTime.now();
        String time = closingTime.substring(0, 10) + "T" + closingTime.substring(11, 16) + ":00.000";
        LocalDateTime closeTime = LocalDateTime.parse(time);
        if (closeTime.isBefore(now))
            return Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "exerciseClosingTime");
        return "OK";
    }

    private String saveFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty
                (String.class, "answerFiles");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(this.client.getUserName()) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
