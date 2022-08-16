package client.network.offlineClient.dataStorage;

import client.gui.EDU;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import shared.model.message.chatMessages.Message;
import shared.response.Response;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MessageDataStorage {
    private final String address;

    public MessageDataStorage() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "messages");
    }

    public void storeData(Response response) {
        File file = makeFile();
        if (file == null) return;

        JSONObject jsonObject = new JSONObject();
        JSONArray messages = new JSONArray();
        for (int i = 0; i < response.getData().size(); i++) {
            Message message = (Message) response.getData("message" + i);
            if (message != null) {
                if (message.isMedia())
                    message.setMessageText(saveFile(message));
                messages.add(message);
            }
        }
        jsonObject.put("messages", messages);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File makeFile() {
        String path = this.address + "/user" + EDU.username + ".json";
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        try {
            if (file.createNewFile())
                return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String saveFile(Message message) {
        String path = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "mediaMessages");
        MediaHandler handler = new MediaHandler();
        path = path + "/" + message.getSender() + "-" + message.getReceiver() + "-" +
                message.getSendMessageTime().replace(":", "-") + "." + message.getFileFormat();
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.writeBytesToFile(path, handler.decode(message.getMessageText()));
        return path;
    }
}
