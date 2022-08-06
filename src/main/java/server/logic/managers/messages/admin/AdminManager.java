package server.logic.managers.messages.admin;

import server.database.dataHandlers.messages.admin.AdminDataHandler;
import server.network.ClientHandler;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class AdminManager {
    private final ClientHandler client;
    private final AdminDataHandler dataHandler;

    public AdminManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new AdminDataHandler(clientHandler.getDataHandler());
    }

    public Response sendMessageToAdmin(Request request) {
        String message = (String) request.getData("message");
        boolean result = this.dataHandler.sendMessageToAdmin(message, client.getUserName());
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

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
