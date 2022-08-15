package server.logic.managers.offlineClient;

import server.database.dataHandlers.offlineClient.ScoreDataHandler;
import server.logic.managers.edu.reportCard.EDUStatusManager;
import server.network.ClientHandler;
import shared.response.Response;

public class ScoreManager {
    private final ClientHandler client;
    private final ScoreDataHandler dataHandler;

    public ScoreManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new ScoreDataHandler(clientHandler.getDataHandler());
    }

    public Response getScores() {
        EDUStatusManager manager = new EDUStatusManager(this.client);
        String studentCode = this.dataHandler.getStudentCode(this.client.getUserName());
        return manager.getAllScores(studentCode);
    }
}
