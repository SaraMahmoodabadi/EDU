package server.logic.managers.edu.registration;

import server.database.dataHandlers.edu.registration.NewUserDataHandler;
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
                String items = getStringFormat(student.getUsername()) + ", " + getStringFormat(student.getStudentCode()) + ", " +
                        student.getEnteringYear() + " ," + getStringFormat(student.getSupervisorCode()) + ", " +
                        getStringFormat(student.getStatus().toString()) + ", " + getStringFormat(student.getGrade().toString());
                if (this.dataHandler.makeNewStudent(items)) {
                   String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "userCreated");
                   return getOKResponse(note);
                }
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
                return getErrorResponse(errorMessage);
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private Response makeProfessor(Request request) {
        Professor professor = (Professor) request.getData("professor");
        if (makeUser(professor, (String) request.getData("profile"))) {
            if (!this.dataHandler.existProfessorCode(professor.getProfessorCode())) {
                String items = getStringFormat(professor.getUsername()) + ", " + getStringFormat(professor.getProfessorCode()) + ", " +
                        getStringFormat(String.valueOf(professor.getRoomNumber())) + ", " +
                        getStringFormat(professor.getDegree().toString()) + ", " +
                        getStringFormat(professor.getType().toString()) + ", ";
                if (this.dataHandler.makeNewProfessor(items)) {
                    String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "userCreated");
                    return getOKResponse(note);
                }
                String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
                return getErrorResponse(errorMessage);
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private boolean makeUser(User user, String profile) {
        if (this.dataHandler.existNationalCode(String.valueOf(user.getNationalCode())) ||
                this.dataHandler.existUsername(user.getUsername())) return false;
        String items = getStringFormat(user.getFirstName()) + ", " + getStringFormat(user.getLastName()) + ", " +
                getStringFormat(String.valueOf(user.getNationalCode())) + ", " + getStringFormat(user.getCollegeCode()) + ", " +
                getStringFormat(user.getUsername()) + ", " + getStringFormat(user.getPassword()) + ", " +
                getStringFormat(user.getEmailAddress()) + ", " + getStringFormat(String.valueOf(user.getPhoneNumber()));
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "userImage");
        new ImageHandler().saveImage(path, profile);
        items = items + ", " + getStringFormat(path);
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

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
