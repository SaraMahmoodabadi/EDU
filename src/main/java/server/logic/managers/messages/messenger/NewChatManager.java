package server.logic.managers.messages.messenger;

import server.database.dataHandlers.messages.messenger.NewChatDataHandler;
import server.network.ClientHandler;
import shared.model.message.request.Type;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.util.ArrayList;
import java.util.List;

public class NewChatManager {
    private final ClientHandler client;
    private final NewChatDataHandler dataHandler;

    public NewChatManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new NewChatDataHandler(clientHandler.getDataHandler());
    }

    public Response sendMessage(Request request) {
        String message = (String) request.getData("message");
        String file = (String) request.getData("file");
        int t = 0;
        int l = 0;
        for (int i = 0; i < request.getData().size(); i++) {
            User user = (User) request.getData("user" + i);
            if (user != null) {
                if (message != null) {
                    l++;
                    boolean result = this.dataHandler.sendMessage(client.getUserName(), client.getUserType().toString(),
                            user.getUsername(), user.getUserType().toString(), message, false);
                    if (result) t++;
                }
                if (file != null) {
                    l++;
                    boolean result = this.dataHandler.sendMessage(client.getUserName(), client.getUserType().toString(),
                            user.getUsername(), user.getUserType().toString(), file, true);
                    if (result) t++;
                }
            }
        }
        if (t == l) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    public Response sendRequest(Request request) {
        UserType user = UserType.valueOf((String) request.getData("user"));
        String userCode = (String) request.getData("userCode");
        String receiver = this.dataHandler.findUsername(user.toString().toLowerCase(), userCode);
        if (receiver == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "invalidInputs");
            return sendErrorResponse(error);
        }
        else {
            boolean result = this.dataHandler.sendRequest(receiver, this.client.getUserName(), Type.SEND_MESSAGE);
            if (result) {
                Response response = new Response(ResponseStatus.OK);
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                response.setNotificationMessage(note);
                return response;
            }
            else {
                String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
                return sendErrorResponse(error);
            }
        }
    }

    public Response getUsers(Request request) {
        String collegeCode = (String) request.getData("collegeCode");
        if (this.client.getUserType() == UserType.STUDENT) {
            return getStudentUsers(collegeCode);
        }
        else {
            shared.model.user.professor.Type professorType =
                    shared.model.user.professor.Type.valueOf((String) request.getData("professorType"));
            switch (professorType) {
                case DEAN:
                case EDUCATIONAL_ASSISTANT:
                    return getCollegeStudent(collegeCode);
                case PROFESSOR:
                    return getProfessorUsers();
                default:
                    String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
                    return sendErrorResponse(error);
            }
        }
    }

    private Response getStudentUsers(String collegeCode) {
        List<User> sameCollegeStudents = this.dataHandler.getCollegeStudents(collegeCode);
        int year = this.dataHandler.findEnteringYear(this.client.getUserName());
        Grade grade = this.dataHandler.findGrade(this.client.getUserName());
        List<User> sameYearStudents = this.dataHandler.getSameYearStudents(year, grade);
        User supervisor = this.dataHandler.findSupervisor(this.client.getUserName());
        List<User> finalList = new ArrayList<>();
        finalList.add(supervisor);
        finalList.addAll(sameCollegeStudents);
        List<String> usernames = new ArrayList<>();
        for (User user : sameCollegeStudents) {
            usernames.add(user.getUsername());
        }
        for (User user : sameYearStudents) {
            if (!usernames.contains(user.getUsername())) finalList.add(user);
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < finalList.size(); i++) {
            response.addData("user" + i, finalList.get(i));
        }
        return response;
    }

    private Response getProfessorUsers() {
        String professorCode = this.dataHandler.findProfessorCode(this.client.getUserName());
        List<User> students = this.dataHandler.getProfessorStudents(professorCode);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < students.size(); i++) {
            response.addData("user" + i, students.get(i));
        }
        return response;
    }

    private Response getCollegeStudent(String collegeCode) {
        List<User> students = this.dataHandler.getCollegeStudents(collegeCode);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < students.size(); i++) {
            response.addData("user" + i, students.get(i));
        }
        return response;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
