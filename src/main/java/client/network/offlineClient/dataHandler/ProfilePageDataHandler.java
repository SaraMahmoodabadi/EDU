package client.network.offlineClient.dataHandler;

import client.gui.EDU;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

import java.io.FileReader;

public class ProfilePageDataHandler {
    private final String address;

    public ProfilePageDataHandler() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
    }

    public Response getProfile() {
        if (EDU.userType == UserType.STUDENT) return getStudent();
        else return getProfessor();
    }

    private Response getStudent() {
        String path = this.address + "/user" + EDU.username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Gson gson = new Gson();
            Student student = gson.fromJson(jo.get("user").toString(), Student.class);
            if (student != null) {
                Response response = new Response(ResponseStatus.OK);
                response.addData("student", student);
                String profile = new ImageHandler().encode(student.getImageAddress());
                response.addData("profile", profile);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
        return getErrorResponse(errorMessage);
    }

    private Response getProfessor() {
        String path = this.address + "/user" + EDU.username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Gson gson = new Gson();
            Professor professor = gson.fromJson(jo.get("user").toString(), Professor.class);
            if (professor != null) {
                Response response = new Response(ResponseStatus.OK);
                response.addData("professor", professor);
                String profile = new ImageHandler().encode(professor.getImageAddress());
                response.addData("profile", profile);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "errorMessage");
        return getErrorResponse(errorMessage);
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
