package server.logic.managers.messages.messenger;

import server.database.dataHandlers.messages.messenger.ChatDataHandler;
import server.network.ClientHandler;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;
import shared.util.media.MediaHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatManager {
    private final ClientHandler client;
    private final ChatDataHandler dataHandler;

    public ChatManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new ChatDataHandler(clientHandler.getDataHandler());
    }

    public Response getAllChats() {
        List<Message> chats = this.dataHandler.getAllChats(this.client.getUserName());
        List<String> times = new ArrayList<>();
        for (Message message : chats) times.add(message.getSendMessageTime());
        List<String> sortedTimes = getSortedTimes(times);
        List<Message> finalList = new ArrayList<>();
        for (String time : sortedTimes) {
            for (Message message : chats)
                if (message.getSendMessageTime().equals(time)) finalList.add(message);
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < finalList.size(); i++) {
            response.addData("message" + i, finalList.get(i));
        }
        return response;
    }

    public Response getChat(Request request) {
        String user = (String) request.getData("user");
        List<Message> messages = this.dataHandler.getChat(this.client.getUserName(), user);
        List<String> times = new ArrayList<>();
        for (Message message : messages) times.add(message.getSendMessageTime());
        List<String> sortedTimes = getSortedTimes(times);
        List<Message> finalList = new ArrayList<>();
        for (String time : sortedTimes) {
            for (Message message : messages)
                if (message.getSendMessageTime().equals(time)) finalList.add(message);
        }
        Collections.reverse(finalList);
        Response response = new Response(ResponseStatus.OK);
        String name = this.dataHandler.getName(user);
        response.addData("name", name);
        String image = this.dataHandler.getImage(user);
        response.addData("profileImage", new ImageHandler().encode(image));
        for (int i = 0; i < finalList.size(); i++) {
            response.addData("message" + i, finalList.get(i));
        }
        return response;
    }

    public Response sendMessage(Request request) {
        String user = (String) request.getData("username");
        String message = (String) request.getData("message");
        boolean isMedia = (boolean) request.getData("isMedia");
        if (isMedia) {
            String fileFormat = (String) request.getData("fileFormat");
            message = saveFile(message, fileFormat);
        }
        boolean result1 = this.dataHandler.sendMessage(this.client.getUserName(), user, message, isMedia);
        boolean result2 = this.dataHandler.updateChat(user, this.client.getUserName(), message);
        if (result1 && result2) {
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

    private String saveFile(String file, String fileFormat) {
        String path = Config.getConfig(ConfigType.SERVER_PATH).getProperty(String.class, "chatFiles");
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
