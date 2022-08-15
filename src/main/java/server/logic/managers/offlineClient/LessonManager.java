package server.logic.managers.offlineClient;

import server.database.dataHandlers.offlineClient.LessonDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.util.ArrayList;

public class LessonManager {
    private final ClientHandler client;
    private final LessonDataHandler dataHandler;

    public LessonManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new LessonDataHandler(clientHandler.getDataHandler());
    }

    public Response getLessons() {
        ArrayList<Lesson> lessons = this.dataHandler.getLessons(this.client.getUserName());
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < lessons.size(); i++) {
            response.addData("lesson" + i, lessons.get(i));
        }
        return response;
    }
}
