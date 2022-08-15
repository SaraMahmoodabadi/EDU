package client.network.offlineClient.dataHandler;

import client.gui.EDU;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatDataHandler {
    private final String chatAddress;
    private final String messageAddress;

    public ChatDataHandler() {
        this.chatAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "chats");
        this.messageAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "messages");
    }

    public Response getChats() {
        List<Message> chats = getChats(EDU.username);
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
        List<Message> messages = getChatMessages(user);
        List<String> times = new ArrayList<>();
        for (Message message : messages) times.add(message.getSendMessageTime());
        List<String> sortedTimes = getSortedTimes(times);
        List<Message> finalList = new ArrayList<>();
        MediaHandler handler = new MediaHandler();
        for (Message message : messages) {
            if (message.isMedia()) {
                String media = handler.encode(message.getMessageText());
                message.setMessageText(media);
            }
        }
        for (String time : sortedTimes) {
            for (Message message : messages)
                if (message.getSendMessageTime().equals(time)) finalList.add(message);
        }
        Collections.reverse(finalList);
        Response response = new Response(ResponseStatus.OK);
        String name = (String) request.getData("name");
        response.addData("name", name);
        for (int i = 0; i < finalList.size(); i++) {
            response.addData("message" + i, finalList.get(i));
        }
        return response;
    }

    private List<Message> getChats(String username) {
        List<Message> chats = new ArrayList<>();
        String path = this.chatAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jo.get("chats");
            for (Object o : jsonArray) {
                Message chat = (Message) o;
                chats.add(chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chats;
    }

    private List<Message> getChatMessages(String user) {
        List<Message> messages = new ArrayList<>();
        String path = this.messageAddress + "/user" + EDU.username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jo.get("messages");
            for (Object o : jsonArray) {
                Message message = (Message) o;
                if (message.getSender().equals(user) || message.getReceiver().equals(user))
                    messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
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
}
