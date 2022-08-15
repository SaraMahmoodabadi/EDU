package server.logic.managers.offlineClient;

import server.database.dataHandlers.offlineClient.UserDataHandler;
import server.network.ClientHandler;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.MediaHandler;

public class UserManager {
    private final ClientHandler client;
    private final UserDataHandler dataHandler;

    public UserManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new UserDataHandler(clientHandler.getDataHandler());
    }

    public Response getUser() {
        Response response = new Response(ResponseStatus.OK);
        if (client.getUserType() == UserType.STUDENT) {
            Student student = this.dataHandler.getStudent(this.client.getUserName());
            response.addData("student", student);
        }
        else if (client.getUserType() == UserType.PROFESSOR) {
            Professor professor = this.dataHandler.getProfessor(this.client.getUserName());
            response.addData("professor", professor);
        }
        String profile = this.dataHandler.getProfileImage(this.client.getUserName());
        int n = profile.split("\\.").length;
        String fileFormat = profile.split("\\.")[n-1];
        response.addData("profile", new MediaHandler().encode(profile));
        response.addData("fileFormat", fileFormat);
        return response;
    }
}
