package server.logic.managers.messages.messenger;

import server.database.dataHandlers.messages.messenger.NewChatDataHandler;
import server.network.ClientHandler;
import shared.model.user.User;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class NewChatManager {
    private final ClientHandler client;
    private final NewChatDataHandler dataHandler;

    public NewChatManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new NewChatDataHandler(clientHandler.getDataHandler());
    }

    public Response sendMessage(Request request) {
        String message = (String) request.getData("message");
        String file = (String) request.getData("file");
        int t = 0;
        int l = 0;
        for (int i = 0; i < request.getData().size(); i++) {
            User user = (User) request.getData("user" + i);
            if (user != null) {
                if (message != null) {
                    l++;
                    boolean result = this.dataHandler.sendMessage(client.getUserName(), client.getUserType().toString(),
                            user.getUsername(), user.getUserType().toString(), message, false);
                    if (result) t++;
                }
                if (file != null) {
                    l++;
                    boolean result = this.dataHandler.sendMessage(client.getUserName(), client.getUserType().toString(),
                            user.getUsername(), user.getUserType().toString(), file, true);
                    if (result) t++;
                }
            }
        }
        if (t == l) {
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

    public Response sendRequest(Request request) {
        return null;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
