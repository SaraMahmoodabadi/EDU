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
import shared.util.media.MediaHandler;

public class NewUserManager {
    private final NewUserDataHandler dataHandler;
    private String items;

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
        String profile = (String) request.getData("profile");
        String fileFormat = (String) request.getData("fileFormat");
        if (makeUser(student, profile, fileFormat)) {
            if (!this.dataHandler.existStudentCode(student.getStudentCode())) {
                if (this.dataHandler.makeNewUser(this.items)) {
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
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private Response makeProfessor(Request request) {
        Professor professor = (Professor) request.getData("professor");
        String profile = (String) request.getData("profile");
        String fileFormat = (String) request.getData("fileFormat");
        if (makeUser(professor, profile, fileFormat)) {
            if (!this.dataHandler.existProfessorCode(professor.getProfessorCode())) {
                if (this.dataHandler.makeNewUser(this.items)) {
                    String items = getStringFormat(professor.getUsername()) + ", " + getStringFormat(professor.getProfessorCode()) + ", " +
                            getStringFormat(String.valueOf(professor.getRoomNumber())) + ", " +
                            getStringFormat(professor.getDegree().toString()) + ", " +
                            getStringFormat(professor.getType().toString());
                    if (this.dataHandler.makeNewProfessor(items)) {
                        String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "userCreated");
                        return getOKResponse(note);
                    }
                    String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
                    return getErrorResponse(errorMessage);
                }
            }
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "duplicateInputs");
        return getErrorResponse(errorMessage);
    }

    private boolean makeUser(User user, String profile, String fileFormat) {
        if (this.dataHandler.existNationalCode(String.valueOf(user.getNationalCode())) ||
                this.dataHandler.existUsername(user.getUsername())) return false;
        this.items = getStringFormat(user.getFirstName()) + ", " + getStringFormat(user.getLastName()) + ", " +
                getStringFormat(user.getUserType().toString()) + ", " +
                getStringFormat(String.valueOf(user.getNationalCode())) + ", " + getStringFormat(user.getCollegeCode()) + ", " +
                getStringFormat(user.getUsername()) + ", " + getStringFormat(user.getPassword()) + ", " +
                getStringFormat(user.getEmailAddress()) + ", " + getStringFormat(String.valueOf(user.getPhoneNumber()));
        String path = saveFile(profile, fileFormat, user.getUsername());
        this.items = this.items + ", " + getStringFormat(path);
        return true;
    }

    private String saveFile(String file, String fileFormat, String username) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "userImage");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(username) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
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
