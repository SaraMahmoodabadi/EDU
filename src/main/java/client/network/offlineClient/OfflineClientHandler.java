package client.network.offlineClient;

import client.Client;
import client.network.ServerController;
import client.network.offlineClient.dataHandler.*;
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
            case SHOW_EXAM_LIST_PAGE:
                LessonDataHandler lessonDataHandler = new LessonDataHandler();
                return lessonDataHandler.getLessons();
            case SHOW_EDU_STATUS_PAGE:
                EduStatusDataHandler eduStatusDataHandler = new EduStatusDataHandler();
                return eduStatusDataHandler.getEduStatus();
            case SHOW_PROFILE_PAGE:
                ProfilePageDataHandler profilePageDataHandler = new ProfilePageDataHandler();
                return profilePageDataHandler.getProfile();
            case SHOW_ALL_CHATS:
                ChatDataHandler chatDataHandler = new ChatDataHandler();
                return chatDataHandler.getChats();
            case SHOW_CHAT:
                chatDataHandler = new ChatDataHandler();
                return chatDataHandler.getChat(request);
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
