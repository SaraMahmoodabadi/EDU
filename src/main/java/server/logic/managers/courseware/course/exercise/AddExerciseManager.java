package server.logic.managers.courseware.course.exercise;

import server.database.dataHandlers.courseware.course.exercise.AddExerciseDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.exercise.Exercise;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

public class AddExerciseManager {
    private final ClientHandler client;
    private final AddExerciseDataHandler dataHandler;

    public AddExerciseManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new AddExerciseDataHandler(clientHandler.getDataHandler());
    }

    public Response addExercise(Request request) {
        Exercise exercise = (Exercise) request.getData("exercise");
        String fileFormat = (String) request.getData("fileFormat");
        String path = saveFile(exercise.getFileAddress(), fileFormat);
        exercise.setFileAddress(path);
        boolean result = this.dataHandler.addExerciseData(exercise);
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

    private String saveFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "exerciseFiles");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(this.client.getUserName()) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
    }
}
