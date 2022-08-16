package client.network.offlineClient.dataStorage;

import client.gui.EDU;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import shared.model.university.lesson.Lesson;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LessonDataStorage {
    private final String address;

    public LessonDataStorage() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "lessons");
    }

    public void storeData(Response response) {
        if (response.getData() == null) return;
        if (response.getStatus() == ResponseStatus.ERROR) return;
        File file = makeFile();
        if (file == null) return;
        JSONObject jsonObject = new JSONObject();
        JSONArray lessons = new JSONArray();
        for (int i = 0; i < response.getData().size(); i++) {
            Lesson lesson = (Lesson) response.getData("lesson" + i);
            if (lesson != null) {
                lessons.add(lesson);
            }
        }
        jsonObject.put("lessons", lessons);
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
}
