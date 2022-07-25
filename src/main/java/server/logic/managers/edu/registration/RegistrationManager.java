package server.logic.managers.edu.registration;

import server.database.dataHandlers.MainDataHandler;
import server.database.dataHandlers.RegistrationDataHandler;
import server.database.dataHandlers.UserHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Lesson;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.List;

public class RegistrationManager {
    private final RegistrationDataHandler dataHandler;

    public RegistrationManager(ClientHandler clientHandler) {
       this.dataHandler = new RegistrationDataHandler(clientHandler.getDataHandler());
    }

    public Response getAllLessons() {
        List<Lesson> lessons = dataHandler.getAllLessons();
        if (lessons != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < lessons.size(); i++) {
                response.addData("lesson" + i, lessons.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    public Response getDesiredLessons(Request request) {
        String collegeName = (String) request.getData("collegeName");
        String unitNumber = (String) request.getData("unitNumber");
        String lessonCode = (String) request.getData("lessonCode");
        String query = " WHERE ";
        int n = 0;
        if (collegeName != null && !collegeName.equals("-")) {
            String collegeCode = this.dataHandler.getCollegeCode(collegeName);
            query += ("collegeCode = " + collegeCode);
            n++;
        }
        if (unitNumber != null) {
            if (n == 1) query += "AND ";
            query += ("unitNumber = " + unitNumber);
            n++;
        }
        if (lessonCode != null) {
            if (n >= 1) query += "AND ";
            query += ("lessonCode = " + lessonCode);
        }
        if (n == 0) return getAllLessons();
        else {
           return makeDesiredLessonsResponse(query);
        }
    }

    private Response makeDesiredLessonsResponse(String query) {
        List<Lesson> lessons = this.dataHandler.getDesiredLessons(query);
        if (lessons != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < lessons.size(); i++) {
                response.addData("lesson" + i, lessons.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    //TODO
    public Response getProfessors() {
        return null;
    }

    //TODO
    public Response editProfessor() {
        return null;
    }

    //TODO
    public Response editLesson() {
        return null;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
