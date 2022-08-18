package client.network.offlineClient;

import client.Client;
import client.gui.EDU;
import client.network.ServerController;
import client.network.offlineClient.dataHandler.*;
import client.network.offlineClient.dataStorage.*;
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
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

    public static void requestGetData() {
        if (EDU.userType == UserType.EDU_ADMIN || EDU.userType == UserType.MR_MOHSENI)
            return;
        Thread thread = new Thread(() -> {
            while (EDU.isOnline) {
                if (EDU.username == null) break;
                Request request;
                Response response;

                request = new Request(RequestType.GET_USER_INFO);
                response = EDU.serverController.sendRequest(request);
                UserDataStorage userDataStorage = new UserDataStorage();
                userDataStorage.storeData(response);

                request = new Request(RequestType.GET_USER_LESSONS);
                response = EDU.serverController.sendRequest(request);
                LessonDataStorage lessonDataStorage = new LessonDataStorage();
                lessonDataStorage.storeData(response);

                if (EDU.userType == UserType.STUDENT) {
                    request = new Request(RequestType.GET_USER_SCORES);
                    response = EDU.serverController.sendRequest(request);
                    ScoreDataStorage scoreDataStorage = new ScoreDataStorage();
                    scoreDataStorage.storeData(response);
                }

                request = new Request(RequestType.GET_USER_CHATS);
                response = EDU.serverController.sendRequest(request);
                ChatDataStorage chatDataStorage = new ChatDataStorage();
                chatDataStorage.storeData(response);

                request = new Request(RequestType.GET_USER_LAST_MESSAGES);
                response = EDU.serverController.sendRequest(request);
                MessageDataStorage messageDataStorage = new MessageDataStorage();
                messageDataStorage.storeData(response);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void connectToServer() {
        ServerController.edu.close();
        int port = Integer.parseInt(Config.getConfig(ConfigType.NETWORK).getProperty(String.class, "clientPort"));
        Client client = new Client(port);
        client.start();
    }

}
