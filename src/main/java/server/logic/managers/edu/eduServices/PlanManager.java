package server.logic.managers.edu.eduServices;

import server.database.dataHandlers.PlanDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.List;

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

    public Response examList() {
        return null;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
