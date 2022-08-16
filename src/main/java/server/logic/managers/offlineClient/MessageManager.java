package server.logic.managers.offlineClient;

import server.database.dataHandlers.messages.messenger.ChatDataHandler;
import server.network.ClientHandler;
import shared.model.message.chatMessages.Message;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.MediaHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageManager {
    private final ClientHandler client;
    private final ChatDataHandler dataHandler;

    public MessageManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new ChatDataHandler(clientHandler.getDataHandler());
    }

    public Response getLastMessages() {
        List<Message> chats = this.dataHandler.getAllChats(this.client.getUserName());
        List<Message> messages = new ArrayList<>();
        for (Message message : chats) {
            List<Message> messageList = getLast10Messages(
                    this.dataHandler.getChat(this.client.getUserName(), message.getUser()));
            messages.addAll(messageList);
        }
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < messages.size(); i++) {
            response.addData("message" + i, messages.get(i));
        }
        return response;
    }

    private List<Message> getLast10Messages(List<Message> messages) {
        List<String> times = new ArrayList<>();
        for (Message message : messages) times.add(message.getSendMessageTime());
        List<String> sortedTimes = getSortedTimes(times);
        List<Message> finalList = new ArrayList<>();
        MediaHandler handler = new MediaHandler();
        for (Message message : messages) {
            if (message.isMedia()) {
                int n = message.getMessageText().split("\\.").length;
                String fileFormat = message.getMessageText().split("\\.")[n-1];
                String media = handler.encode(message.getMessageText());
                message.setMessageText(media);
                message.setFileFormat(fileFormat);
            }
            if (message.isTransmitter()) {
                message.setSender(this.client.getUserName());
                message.setReceiver(message.getUser());
            }
            else {
                message.setReceiver(this.client.getUserName());
                message.setSender(message.getUser());
            }
        }
        for (String time : sortedTimes) {
            for (Message message : messages)
                if (message.getSendMessageTime().equals(time)) finalList.add(message);
        }
        return finalList;
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
        ArrayList<String> list = new ArrayList<>(Arrays.asList(sortedTimes));
        if (list.size() < 10) return list;
        ArrayList<String> finalList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            finalList.add(list.get(i));
        }
        return finalList;
    }
}
