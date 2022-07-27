package server.logic.managers.edu.registration;

import server.database.dataHandlers.NewUserDataHandler;
import server.network.ClientHandler;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

public class NewUserManager {
    private final NewUserDataHandler dataHandler;

    public NewUserManager(ClientHandler client) {
        this.dataHandler = new NewUserDataHandler(client.getDataHandler());
    }

    public Response makeUser(Request request) {
        if (request.getData("userType") == UserType.STUDENT) {
            return makeStudent(request);
        }
        else {
            return makeProfessor(request);
        }
    }

    private Response makeStudent(Request request) {
        Student student = (Student) request.getData("student");
        if (makeUser(student, (String) request.getData("profile"))) {
            if (!this.dataHandler.existStudentCode(student.getStudentCode())) {
                String items = student.getUsername() + ", " + student.getStudentCode() + ", " +
                        student.getEnteringYear() + " ," + student.getSupervisorCode() + ", " +
                        student.getStatus() + ", " + student.getGrade();
                if (this.dataHandler.makeNewStudent(items)) {
                   String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("userCreated");
                   return getOKResponse(note);
                }
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
                return getErrorResponse(errorMessage);
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private Response makeProfessor(Request request) {
        Professor professor = (Professor) request.getData("professor");
        if (makeUser(professor, (String) request.getData("profile"))) {
            if (!this.dataHandler.existStudentCode(professor.getProfessorCode())) {
                String items = professor.getUsername() + ", " + professor.getProfessorCode() + ", " +
                        professor.getRoomNumber() + ", " + professor.getDegree() + ", " +
                        professor.getType() + ", ";
                if (this.dataHandler.makeNewProfessor(items)) {
                    String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("userCreated");
                    return getOKResponse(note);
                }
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("error");
                return getErrorResponse(errorMessage);
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty("duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private boolean makeUser(User user, String profile) {
        if (this.dataHandler.existNationalCode(String.valueOf(user.getNationalCode())) ||
                this.dataHandler.existUsername(user.getUsername())) return false;
        String items = user.getFirstName() + ", " + user.getLastName() + ", " +
                user.getNationalCode() + ", " + user.getCollegeCode() + ", " +
                user.getUsername() + ", " + user.getPassword() + ", " +
                user.getEmailAddress() + ", " + user.getPhoneNumber();
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty("userImage");
        new ImageHandler().saveImage(path, profile);
        items = items + ", " + path;
        return this.dataHandler.makeNewUser(items);
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    private Response getOKResponse(String note) {
        Response response = new Response(ResponseStatus.OK);
        response.setNotificationMessage(note);
        return response;
    }

}
