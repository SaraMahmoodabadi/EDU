package server.logic.managers.edu.profile;

import server.database.dataHandlers.profile.ProfileDataHandler;
import server.network.ClientHandler;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

public class ProfileManager {
    private final ClientHandler client;
    private final ProfileDataHandler dataHandler;

    public ProfileManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new ProfileDataHandler(clientHandler.getDataHandler());
    }

    public Response getInformation() {
        if (this.client.getUserType() == UserType.STUDENT)
            return getStudentInfo();
        else return getProfessorInfo();
    }

    public Response changeEmailAddress(Request request) {
        boolean result = this.dataHandler.updateEmail(this.client.getUserName(),
                (String) request.getData("email"));
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
        return getErrorResponse(errorMessage);
    }

    public Response changePhoneNumber(Request request) {
        boolean result = this.dataHandler.updatePhone(this.client.getUserName(),
                (String) request.getData("phoneNumber"));
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
        return getErrorResponse(errorMessage);
    }

    private Response getStudentInfo() {
        Student student = this.dataHandler.getStudentInfo(this.client.getUserName());
        if (student != null) {
            Response response = new Response(ResponseStatus.OK);
            response.addData("student", student);
            String profile = new ImageHandler().encode(student.getImageAddress());
            response.addData("profile", profile);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
        return getErrorResponse(errorMessage);
    }

    private Response getProfessorInfo() {
        Professor professor = this.dataHandler.getProfessorInfo(this.client.getUserName());
        if (professor != null) {
            Response response = new Response(ResponseStatus.OK);
            response.addData("professor", professor);
            String profile = new ImageHandler().encode(professor.getImageAddress());
            response.addData("profile", profile);
            return response;
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
        return getErrorResponse(errorMessage);
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
