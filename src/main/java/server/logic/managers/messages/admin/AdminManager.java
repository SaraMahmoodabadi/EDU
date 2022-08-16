package server.logic.managers.messages.admin;

import server.database.dataHandlers.messages.admin.AdminDataHandler;
import server.network.ClientHandler;
import shared.model.message.chatMessages.Message;
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
import java.util.Map;

public class AdminManager {
    private final ClientHandler client;
    private final AdminDataHandler dataHandler;

    public AdminManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new AdminDataHandler(clientHandler.getDataHandler());
    }

    public Response sendMessageToAdmin(Request request) {
        String message = (String) request.getData("message");
        String file = (String) request.getData("file");
        String fileFormat = (String) request.getData("fileFormat");
        String filePath = null;
        if (file != null) filePath = saveUserFile(file, fileFormat);
        boolean result = false;
        if (filePath != null && message != null)  {
            result = this.dataHandler.sendMixMessageToAdmin(message, filePath, client.getUserName());
        }
        else if (message != null) {
            result = this.dataHandler.sendTextMessageToAdmin(message, client.getUserName());
        }
        else if (filePath != null) {
            result = this.dataHandler.sendMediaMessageToAdmin(filePath, client.getUserName());
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

    public Response sendAnswer(Request request) {
        String answer = String.valueOf(request.getData("answer"));
        String username = String.valueOf(request.getData("username"));
        String userMessage = String.valueOf(request.getData("userMessage"));
        String time = String.valueOf(request.getData("messageTime"));
        boolean isMedia = (boolean) request.getData("isMedia");
        String fileFormat = String.valueOf(request.getData("fileFormat"));
        List<String> answers;
        if (isMedia)
            answers = this.dataHandler.getMediaAnswers(username, time);
        else
            answers = this.dataHandler.getMessageAnswers(username, time);
        if (answers == null) {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
        if (isMedia) answer = saveAdminFile(answer, fileFormat);
        answers.add(answer);
        boolean result;
        if (isMedia)
            result = this.dataHandler.updateMediaAnswers(username,userMessage, time, answers);
        else
            result = this.dataHandler.updateTextAnswers(username, userMessage, time, answers);
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

    public Response showAllMessages() {
        List<Message> messages = this.dataHandler.getAllMessages();
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
        String userMessage;
        String time = String.valueOf(request.getData("time"));
        Map<String, String> messages = this.dataHandler.getMessage(user, time);
        String textMessage = messages.get("message");
        String media = messages.get("media");
        List<String> answers = this.dataHandler.getMessageAnswers(user, time);
        List<String> mediaAnswers = this.dataHandler.getMediaAnswers(user, time);
        Response response = new Response(ResponseStatus.OK);
        String name = this.dataHandler.getName(user);
        response.addData("name", name);
        MediaHandler handler = new MediaHandler();
        int t = 0;
        if (textMessage != null) {
            Message message = new Message(user, textMessage, false, false);
            response.addData("message" + t, message);
            t++;
        }
        if (media != null) {
            userMessage = handler.encode(media);
            Message message = new Message(user, userMessage, false, true);
            response.addData("message" + t, message);
            t++;
        }
        int m = t;
        for (int i = t; i < answers.size() + t ; i++) {
            Message message = new Message("1", answers.get(i - t), true, false);
            response.addData("message" + i, message);
            m++;
        }
        for (int i = m; i < mediaAnswers.size() + m ; i++) {
            String answer = handler.encode(mediaAnswers.get(i - m));
            Message message = new Message("1", answer, true, true);
            response.addData("message" + i, message);
        }
        return response;
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

    private String saveUserFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "userFiles");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(this.client.getUserName()) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
    }

    private String saveAdminFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "adminFiles");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + handler.createNameByUser(this.client.getUserName()) + "." + fileFormat;
        handler.writeBytesToFile(path, handler.decode(file));
        return path;
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
