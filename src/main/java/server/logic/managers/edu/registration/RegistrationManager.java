package server.logic.managers.edu.registration;

import server.database.dataHandlers.RegistrationDataHandler;
import server.network.ClientHandler;
import shared.model.university.lesson.Group;
import shared.model.university.lesson.Lesson;
import shared.model.user.professor.Professor;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.List;

public class RegistrationManager {
    private final RegistrationDataHandler dataHandler;
    private final ClientHandler clientHandler;

    public RegistrationManager(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
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

    public Response addLesson(Request request) {
        Lesson lesson = (Lesson) request.getData("lesson");
        if (this.dataHandler.existLesson(lesson.getLessonCode())) {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("duplicateLessonCode");
            return getErrorResponse(errorMessage);
        }
        else {
            if (this.dataHandler.makeLesson(lesson)) {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("lessonCreated");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
            else {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
                return getErrorResponse(errorMessage);
            }
        }
    }

    public Response editLesson(Request request) {
        String lessonCode = (String) request.getData("lessonCode");
        String collegeCode = (String) request.getData("collegeCode");
        if (this.dataHandler.existLesson(lessonCode) &&
                collegeCode.equals(this.dataHandler.getLessonCollegeCode(lessonCode))) {
            String examTime = (String) request.getData("examTime");
            String days = request.getData("days").toString();
            String classTime = (String) request.getData("classTime");
            Group group = (Group) request.getData("group");
            String query = "";
            int n = 0;
            if (classTime != null) {
                query += ("classTime = " + classTime);
                n++;
            }
            if (days != null) {
                if (n == 1) query += ", ";
                query += ("days = " + days);
                n++;
            }
            if (examTime != null) {
                if (n >= 1) query += ", ";
                query += ("examTime = " + examTime);
            }
            if (group != null) {
                boolean result = this.dataHandler.makeGroup(group);
                if (!result) {
                    String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
                    return getErrorResponse(errorMessage);
                }
            }
            if (n != 0) {
                boolean result = this.dataHandler.editLesson(query, lessonCode);
                if (result) {
                    String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("lessonEdited");
                    Response response = new Response(ResponseStatus.OK);
                    response.setNotificationMessage(note);
                    return response;
                }
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response removeLesson(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        if (collegeCode.equals(this.dataHandler.getLessonCollegeCode
                ((String) request.getData("lessonCode")))) {
        boolean result;
        if (request.getRequestType() == RequestType.REMOVE_LESSON_GROUP) {
            result = this.dataHandler.removeGroup((String) request.getData("lessonCode"),
                    (String) request.getData("group"));
        }
        else {
            result = this.dataHandler.removeLesson((String) request.getData("lessonCode"));
        }
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("lessonRemoved");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        } }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response getProfessors() {
        List<Professor> professors = this.dataHandler.getAllProfessors();
        if (professors != null) {
            Response response = new Response(ResponseStatus.OK);
            for (int i = 0; i < professors.size(); i++) {
                response.addData("professor" + i, professors.get(i));
            }
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public Response editProfessor(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String phoneNumber = (String) request.getData("phoneNumber");
        String email = (String) request.getData("email");
        String room = (String) request.getData("room");
        String degree = (String) request.getData("degree");
        String collegeCode = (String) request.getData("collegeCode");
        if (collegeCode.equals(this.dataHandler.getProfessorCollegeCode(professorCode))) {
            String query = "";
            int n = 0;
            if (phoneNumber != null) {
                query += ("phoneNumber = " + phoneNumber);
                n++;
            }
            if (email != null) {
                if (n == 1) query += ", ";
                query += ("emailAddress = " + email);
                n++;
            }
            boolean result1 = true;
            if (n != 0) {
                result1 = this.dataHandler.editUser(this.clientHandler.getUserName(), query, professorCode);
            }
            int m = 0;
            if (degree != null) {
                query += ("degree = " + degree);
                m++;
            }
            if (room != null) {
                if (m == 1) query += ", ";
                query += ("roomNumber = " + room);
                m++;
            }
            boolean result2 = true;
            if (m != 0) {
                result2 = this.dataHandler.editProfessor(professorCode, query);
            }
            if ((m == 0 && n == 0) || (result1 && result2)) {
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
                return getErrorResponse(errorMessage);
            }
            else {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("professorEdited");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
        return getErrorResponse(errorMessage);
    }

    public Response deposal(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String collegeCode = (String) request.getData("collegeCode");
        String query = "educationalAssistant = NULL";
        boolean result = this.dataHandler.deposal(professorCode, query, collegeCode);
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
            return getErrorResponse(errorMessage);
        }
    }

    public Response appointment(Request request) {
        String professorCode = (String) request.getData("professorCode");
        String collegeCode = (String) request.getData("collegeCode");
        String query = "educationalAssistant = " + professorCode;
        boolean result = this.dataHandler.appointment(professorCode, query, collegeCode);
        if (result) {
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
            Response response = new Response(ResponseStatus.OK);
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
            return getErrorResponse(errorMessage);
        }
    }

    public Response removeProfessor(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        String professorCode = (String) request.getData("professorCode");
        if (collegeCode.equals(this.dataHandler.getProfessorCollegeCode(professorCode))) {
            boolean result = this.dataHandler.removeProfessor(professorCode);
            if (result) {
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("done");
                Response response = new Response(ResponseStatus.OK);
                response.setNotificationMessage(note);
                return response;
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("invalidInputs");
        return getErrorResponse(errorMessage);
    }
}
