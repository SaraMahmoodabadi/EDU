package client.network.offlineClient;

import client.Client;
import client.network.ServerController;
import client.network.offlineClient.dataHandler.LoginDtaHandler;
import client.network.offlineClient.dataHandler.MainPageDataHandler;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class OfflineClientHandler {

    public Response handleRequest(Request request) {
        switch (request.getRequestType()) {
            case LOGIN:
                LoginDtaHandler dataHandler = new LoginDtaHandler();
                return dataHandler.login(request);
            case SHOW_MAIN_PAGE:
                MainPageDataHandler mainPageDataHandler = new MainPageDataHandler();
                return mainPageDataHandler.getMainPageData();
            case SHOW_WEEKLY_SCHEDULE_PAGE:
                break;
            case SHOW_EXAM_LIST_PAGE:
                break;
            case SHOW_EDU_STATUS_PAGE:
                break;
            case SHOW_PROFILE_PAGE:
                break;
            case SHOW_ALL_CHATS:
                break;
            case SHOW_CHAT:
                break;
        }
        String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "offlineError");
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(error);
        return response;
    }

    public static void connectToServer() {
        ServerController.edu.close();
        int port = Integer.parseInt(Config.getConfig(ConfigType.NETWORK).getProperty(String.class, "clientPort"));
        Client client = new Client(port);
        client.start();
    }

}
