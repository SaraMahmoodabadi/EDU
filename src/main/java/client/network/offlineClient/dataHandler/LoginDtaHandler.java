package client.network.offlineClient.dataHandler;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.model.user.student.EducationalStatus;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.FileReader;
import java.io.IOException;

public class LoginDtaHandler {
    private final String address;

    public LoginDtaHandler() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
    }

    public Response login(Request request) {
        String username = String.valueOf(request.getData("username"));
        String password = String.valueOf(request.getData("password"));
        User user = getInformation(username);
        if (user != null && user.getPassword().equals(password)) {
            return getOKResponse(user);
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "loginError");
            Response response = new Response(ResponseStatus.ERROR);
            response.setErrorMessage(errorMessage);
            return response;
        }
    }

    private Response getOKResponse(User user) {
        Response response = new Response(ResponseStatus.OK);
        response.addData("userType", user.getUserType());
        response.addData("collegeCode", user.getCollegeCode());
        user.setLastLogin(user.getThisLogin());
        if (user.getUserType() == UserType.STUDENT) {
            response.addData("eduStatus", getStatus(user.getUsername()));
        }
        else if (user.getUserType() == UserType.PROFESSOR) {
            response.addData("professorType", getProfessorType(user.getUsername()));
        }
        return response;
    }

    private User getInformation(String username) {
        String path = this.address + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = jo.toJSONString();
            return mapper.readValue(jsonString, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Type getProfessorType(String username) {
        String path = this.address + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Professor professor = (Professor) jo.get("user");
            return professor.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Type.PROFESSOR;
    }

    private EducationalStatus getStatus(String username) {
        String path = this.address + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Student student = (Student) jo.get("user");
            return student.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EducationalStatus.STUDYING;
    }
}
