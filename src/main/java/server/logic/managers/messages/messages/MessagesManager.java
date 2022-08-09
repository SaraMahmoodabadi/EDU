package server.logic.managers.messages.messages;

import server.database.dataHandlers.edu.unitSelection.UnitSelectionDataHandler;
import server.database.dataHandlers.messages.messages.MessagesDataHandler;
import server.network.ClientHandler;
import shared.model.message.chatMessages.Message;
import shared.model.message.request.Type;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagesManager {
    private final ClientHandler client;
    private final MessagesDataHandler dataHandler;

    public MessagesManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new MessagesDataHandler(clientHandler.getDataHandler());
    }

    public Response getAllMessages() {
        List<Message> messages = new ArrayList<>();
        messages.addAll(this.dataHandler.getAllSendMessageRequests(this.client.getUserName()));
        messages.addAll(this.dataHandler.getAllAdminMessages(this.client.getUserName()));
        if (this.client.getUserType() == UserType.STUDENT)
            messages.addAll(this.dataHandler.getAllMohseniMessages(this.client.getUserName()));
        else messages.addAll(this.dataHandler.getAllProfessorRequests(this.client.getUserName()));
        List<String> times = new ArrayList<>();
        for (Message message : messages) times.add(message.getSendMessageTime());
        List<String> sortedTimes = getSortedTimes(times);
        List<Message> finalList = new ArrayList<>();
        for (String time : sortedTimes) {
            for (Message message : messages)
                if (message.getSendMessageTime().equals(time)) finalList.add(message);
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < finalList.size(); i++) {
            response.addData("message" + i, finalList.get(i));
        }
        return response;
    }

    public Response getMessage(Request request) {
        String user = String.valueOf(request.getData("user"));
        String userMessage = String.valueOf(request.getData("message"));
        String time = String.valueOf(request.getData("time"));
        String type = String.valueOf(request.getData("type"));
        String name = String.valueOf(request.getData("name"));
        switch (type) {
            case "request":
                return showRequest(user, name, userMessage, time);
            case "mohseniMessage":
                return showMohseniMessage(time, name);
            case "adminMessage":
                return showAdminMessage(time, name);
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    public Response getRequestResult(Request request) {
        String username = String.valueOf(request.getData("username"));
        boolean requestResult = (boolean) request.getData("result");
        String date = String.valueOf(request.getData("date"));
        String userMessage = String.valueOf(request.getData("message"));
        boolean result = false;
        if (userMessage.equals("request : " + Type.SEND_MESSAGE)) {
            result = this.dataHandler.setRequestResult(this.client.getUserName(), username, date, requestResult);
            if (requestResult && result)
                this.dataHandler.createChat(username, this.client.getUserName());
        }
        else {
            String professorCode = this.dataHandler.findUserCode("professor", this.client.getUserName());
            String studentCode = this.dataHandler.findUserCode("student", this.client.getUserName());
            if (userMessage.equals("request : " + Type.MINOR)) {
                String eduAssistant = this.dataHandler.getEduAssistant(this.client.getUserName());
                if (professorCode.equals(eduAssistant))
                    result = this.dataHandler.setRequestResult(professorCode, studentCode, date,  Type.MINOR,
                            requestResult, true);
                else  result = this.dataHandler.setRequestResult(professorCode, studentCode, date,  Type.MINOR,
                        requestResult, false);
            }
            else if (userMessage.equals("request : " + Type.WITHDRAWAL)) {
                result = this.dataHandler.setRequestResult(professorCode, studentCode, date,  Type.WITHDRAWAL,
                        requestResult, true);
            }
            else if (userMessage.equals("request : " + Type.TAKE_LESSON)) {
                result = this.dataHandler.setRequestResult(professorCode, studentCode, date,  Type.TAKE_LESSON,
                        requestResult, true);
                if (result && requestResult) {
                    UnitSelectionDataHandler handler = new UnitSelectionDataHandler(this.client.getDataHandler());
                    String lesson = this.dataHandler.findLesson(studentCode, date);
                    handler.takeLesson(lesson, username);
                }
            }
        }
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

    private Response showRequest(String user, String name, String message, String time) {
        String requestText = "";
        if (message.equals("request : " + Type.SEND_MESSAGE)) {
            requestText = getSendMessageRequest(name);
        }
        else if (message.equals("request : " + Type.RECOMMENDATION)) {
            requestText = getRecommendationRequest(user);
        }
        else if (message.equals("request : " + Type.WITHDRAWAL)) {
            requestText = getWithdrawalRequest(user);
        }
        else if (message.equals("request : " + Type.MINOR)) {
            requestText = getMinorRequest(user, time);
        }
        else if (message.equals("request : " + Type.TAKE_LESSON)) {
            requestText = getTakeLessonRequest(user, time);
        }
        Message request = new Message(user, requestText, false);
        Response response = new Response(ResponseStatus.OK);
        response.addData("message", request);
        response.addData("name", name);
        return response;
    }

    private Response showMohseniMessage(String time, String name) {
        List<Message> messages = this.dataHandler.getMohseniMessage(this.client.getUserName(), time);
        Response response = new Response(ResponseStatus.OK);
        response.addData("name", name);
        for (int i = 0; i < messages.size(); i++) {
            response.addData("message" + i, messages.get(i));
        }
        return response;
    }

    private Response showAdminMessage(String time, String name) {
        List<Message> messages = this.dataHandler.getAdminMessage(this.client.getUserName(), time);
        Response response = new Response(ResponseStatus.OK);
        response.addData("name", name);
        for (int i = 0; i < messages.size(); i++) {
            response.addData("message" + i, messages.get(i));
        }
        return response;
    }

    private String getSendMessageRequest(String name) {
        String message = "User %s wants to send you message.";
        message = String.format(message, name);
        return message;
    }

    private String getRecommendationRequest(String user) {
        String message = "Student %s asks you for a recommendation.";
        String studentCode = this.dataHandler.findUserCode("student", user);
        message = String.format(message, studentCode);
        return message;
    }

    private String getWithdrawalRequest(String user) {
        String message = "Student %s requests for withdrawal.";
        String studentCode = this.dataHandler.findUserCode("student", user);
        message = String.format(message, studentCode);
        return message;
    }

    private String getMinorRequest(String user, String date) {
        String message = "Student %s requests for minor. Major : %s";
        String studentCode = this.dataHandler.findUserCode("student", user);
        String major = this.dataHandler.findMinorMajor(studentCode, date);
        message = String.format(message, studentCode, major);
        return message;
    }

    private String getTakeLessonRequest(String user, String date) {
        String message = "Student %s wants to take lesson %s, group %s.";
        String studentCode = this.dataHandler.findUserCode("student", user);
        String lesson = this.dataHandler.findLesson(studentCode, date);
        message = String.format(message, studentCode, getLessonCode(lesson), getGroup(lesson));
        return message;
    }

    private String getLessonCode(String lesson) {
        String term = lesson.split("-")[0];
        int n = lesson.split("-").length;
        String group = lesson.split("-")[n - 1];
        return lesson.substring(term.length() + 1, lesson.length() - group.length() - 1);
    }

    private String getGroup(String lesson) {
        int n = lesson.split("-").length;
        return lesson.split("-")[n - 1];
    }

    private List<String> getSortedTimes(List<String> times) {
        List<String> sortedTimes = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            int t = 0;
            for (String time : times) {
                LocalDateTime date1 = LocalDateTime.parse(times.get(i));
                LocalDateTime date2 = LocalDateTime.parse(time);
                if (date1.isBefore(date2)) t++;
            }
            sortedTimes.set(t, times.get(i));
        }
        return sortedTimes;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
