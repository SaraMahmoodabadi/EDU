package client.network.offlineClient.dataStorage;

import client.gui.EDU;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import shared.model.university.lesson.score.Score;
import shared.response.Response;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScoreDataStorage {
    private final String address;

    public ScoreDataStorage() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "scores");
    }

    public void storeData(Response response) {
        File file = makeFile();
        if (file == null) return;
        JSONObject jsonObject = new JSONObject();
        JSONArray scores = new JSONArray();
        for (int i = 0; i < response.getData().size(); i++) {
            Score score = (Score) response.getData("score" + i);
            if (score != null) {
                scores.add(score);
            }
        }
        jsonObject.put("scores", scores);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File makeFile() {
        String path = this.address + "/user" + EDU.username;
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
