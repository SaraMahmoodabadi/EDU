package client.network.offlineClient.dataHandler;

import client.gui.EDU;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.university.lesson.Lesson;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonDataHandler {
    private final String userAddress;
    private final String lessonAddress;

    public LessonDataHandler() {
        this.userAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
        this.lessonAddress = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "lessons");
    }

    public Response getLessons() {
        String username = EDU.username;
        List<Lesson> lessons = getThisTermLessons(username);
        Response response = new Response(ResponseStatus.OK);
        for (int i = 0; i < lessons.size() ; i++) {
            response.addData("lesson" + i, lessons.get(i));
        }
        return response;
    }

    private List<Lesson> getThisTermLessons(String username) {
        String thisTerm;
        if (registrationPassed(username)) {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "thisTerm");
        }
        else {
            thisTerm = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "lastTerm");
        }
        List<Lesson> lessons = new ArrayList<>();
        String path = this.lessonAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jo.get("lessons");
            Gson gson = new Gson();
            for (Object o : jsonArray) {
                Lesson lesson = gson.fromJson(o.toString(), Lesson.class);
                if (thisTerm.equals(String.valueOf(lesson.getTerm()))) {
                    lessons.add(lesson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lessons;
    }

    private boolean registrationPassed(String username) {
        String time = getRegistrationTime(username);
        if (EDU.userType == UserType.STUDENT) {
            if (time == null) return true;
        } else if (EDU.userType == UserType.PROFESSOR) {
            return true;
        }
        try {
            int y = Integer.parseInt(time.split("-")[0]);
            int m = Integer.parseInt(time.split("-")[1]);
            int d = Integer.parseInt(time.split("-")[2]);
            int h = Integer.parseInt(time.split("-")[4].split(":")[0]);
            int mm = Integer.parseInt(time.split("-")[4].split(":")[1]);
            Calendar calendar = Calendar.getInstance();
            int y2 = calendar.get(Calendar.YEAR);
            int m2 = calendar.get(Calendar.MONTH) + 1;
            int d2 = calendar.get(Calendar.DAY_OF_MONTH);
            int h2 = calendar.get(Calendar.HOUR_OF_DAY);
            int mm2 = calendar.get(Calendar.MINUTE);
            if (y > y2) return false;
            if (y == y2 && m > m2) return false;
            if (y == y2 && m == m2 && d > d2) return false;
            if (y == y2 && m == m2 && d == d2 && h > h2) return false;
            if (y == y2 && m == m2 && d == d2 && h == h2 && mm > mm2) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String getRegistrationTime(String username) {
        String path = this.userAddress + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Gson gson = new Gson();
            Student student = gson.fromJson(jo.get("user").toString(), Student.class);
            return student.getRegistrationTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LocalDateTime.now().toString();
    }
}
