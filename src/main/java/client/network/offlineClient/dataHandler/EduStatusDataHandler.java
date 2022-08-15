package client.network.offlineClient.dataHandler;

import client.gui.EDU;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.university.lesson.score.Score;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EduStatusDataHandler {
    private final String scoreAddress;
    private final String userAddress;

    public EduStatusDataHandler() {
        this.scoreAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "scores");
        this.userAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
    }

    public Response getEduStatus() {
        List<Score> scores = getScores(EDU.username);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < scores.size(); i++) {
            response.addData("score" + i, scores.get(i));
        }
        response.addData("average", getRate(EDU.username));
        response.addData("numberPassed", getNumberOfPassedUnits(EDU.username));
        return response;
    }

    private String getNumberOfPassedUnits(String username) {
        String path = this.scoreAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            return (String) jo.get("numberOfPassedUnits");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getRate(String username) {
        String path = this.userAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Student student = (Student) jo.get("user");
            return String.valueOf(student.getRate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    private List<Score> getScores(String username) {
        List<Score> scores = new ArrayList<>();
        String path = this.scoreAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jo.get("scores");
            for (Object o : jsonArray) {
                Score score = (Score) o;
                scores.add(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }
}
