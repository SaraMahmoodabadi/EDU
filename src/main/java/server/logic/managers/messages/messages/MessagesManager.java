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
import shared.util.media.MediaHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
        messages.addAll(this.dataHandler.getAllSendMessageResults(this.client.getUserName()));
        messages.addAll(this.dataHandler.getAllSystemMessages(this.client.getUserName()));
        if (this.client.getUserType() == UserType.STUDENT) {
            messages.addAll(this.dataHandler.getAllMohseniMessages(this.client.getUserName()));
            messages.addAll(this.dataHandler.getAllRequestsResult(this.client.getUserName()));
            messages.addAll(this.dataHandler.getAllRecommendation(this.client.getUserName()));
        }
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
            case "request result":
                return showRequestResult(user, time, userMessage, name);
            case "systemMessages" :
                return showSystemMessage(time, name);
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
            String studentCode = this.dataHandler.findUserCode("student", username);
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
                if (result && requestResult) this.dataHandler.setWithdrawalStatus(studentCode);
            }
            else if (userMessage.equals("request : " + Type.TAKE_LESSON)) {
                result = this.dataHandler.setRequestResult(professorCode, studentCode, date,  Type.TAKE_LESSON,
                        requestResult, true);
                if (result && requestResult) {
                    UnitSelectionDataHandler handler = new UnitSelectionDataHandler(this.client.getDataHandler());
                    String lesson = this.dataHandler.findLesson(studentCode, date);
                    handler.takeLesson(lesson, username);
                    handler.addStudentToGroup(getLessonCode(lesson), getGroup(lesson), username);
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
        MediaHandler handler = new MediaHandler();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (message.isMedia()) message.setMessageText(handler.encode(message.getMessageText()));
            response.addData("message" + i, message);
        }
        return response;
    }

    private Response showSystemMessage(String time, String name) {
        Message message = this.dataHandler.getSystemMessage(this.client.getUserName(), time);
        if (message == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
        Response response = new Response(ResponseStatus.OK);
        response.addData("name", name);
        response.addData("message", message);
        return response;
    }

    private Response showRequestResult(String user, String time, String message, String name) {
        List<String> results = this.dataHandler.getRequestResult(user, time, this.client.getUserName(),
                Type.valueOf(message.split(" ")[3]));
        String resultText = "";
        if (message.equals("request result : " + Type.SEND_MESSAGE)) {
            resultText = getSendMessageResult(name, results);
        }
        else if (message.equals("request result : " + Type.RECOMMENDATION)) {
            resultText = getRecommendationResult(name, user, results);
        }
        else if (message.equals("request result : " + Type.WITHDRAWAL)) {
            resultText = getWithdrawalResult(name, results);
        }
        else if (message.equals("request result : " + Type.MINOR)) {
            resultText = getMinorResult(results);
        }
        else if (message.equals("request result : " + Type.TAKE_LESSON)) {
            resultText = getTakeLessonResult(name, results);
        }
        Message requestResult = new Message(user, resultText, false);
        Response response = new Response(ResponseStatus.OK);
        response.addData("message", requestResult);
        response.addData("name", name);
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

    private String getRecommendationResult(String name, String user, List<String> results) {
        String result = "Professor %s has given you a recommendation.\nRecommendation text: " +
                "I %s certify that, student with student code %s, passed the lessons %s " +
                "with a grade of %s and also in courses %s Acted as an educational assistant";
        String professorCode = this.dataHandler.findUserCode("professor", user);
        String studentCode = this.dataHandler.findUserCode("student", this.client.getUserName());
        result = String.format(result, professorCode, name, studentCode, results.get(0), results.get(1), results.get(2));
        return result;
    }

    private String getSendMessageResult(String name, List<String> results) {
        String result = "user %s answered your request. Your request: %s, result: %s";
        result = String.format(result, name, Type.SEND_MESSAGE, results.get(0));
        return result;
    }

    private String getWithdrawalResult(String name, List<String> results) {
        String result = "user %s answered your request. Your request: %s, result: %s";
        result = String.format(result, name, Type.WITHDRAWAL, results.get(0));
        return result;
    }

    private String getMinorResult(List<String> results) {
        String result = "Your request result. Your request: %s, your college: %s, other college: %s";
        String result1 = results.get(0) == null ? "-" : results.get(0);
        String result2 = results.get(1) == null ? "-" : results.get(1);
        result = String.format(result, Type.MINOR, result1, result2);
        return result;
    }

    private String getTakeLessonResult(String name, List<String> results) {
        String result = "user %s answered your request. Your request: %s, result: %s";
        result = String.format(result, name, Type.TAKE_LESSON, results.get(0));
        return result;
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
        String[] sortedTimes = new String[times.size()];
        for (int i = 0; i < times.size(); i++) {
            int t = 0;
            LocalDateTime date1 = LocalDateTime.parse(times.get(i));
            for (String time : times) {
                LocalDateTime date2 = LocalDateTime.parse(time);
                if (date1.isBefore(date2)) t++;
            }
            sortedTimes[t] = times.get(i);
        }
        return new ArrayList<>(Arrays.asList(sortedTimes));
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
