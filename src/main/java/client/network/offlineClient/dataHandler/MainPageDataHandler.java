package client.network.offlineClient.dataHandler;

import client.gui.EDU;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.user.User;
import shared.model.user.UserType;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainPageDataHandler {
    private final String address;

    public MainPageDataHandler() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
    }

    public Response getMainPageData() {
        User user = getUser(EDU.username);
        if (user != null) {
            Response response = new Response(ResponseStatus.OK);
            response.addData("name", user.getFullName());
            response.addData("emailAddress", user.getEmailAddress());
            response.addData("lastLogin", user.getLastLogin());
            response.addData("profileImage", new ImageHandler().encode(user.getImageAddress()));
            if (EDU.userType == UserType.STUDENT) return setTableData(response);
            else return response;
        }
        else {
            String errorMessage = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                    (String.class, "errorMessage");
            return getErrorResponse(errorMessage);
        }
    }

    private Response setTableData(Response response) {
        response.addData("isUnitSelectionTime", false);
        List<String> table = getTableData(EDU.username);
        if (table != null) {
            List<String> middleList = new ArrayList<>();
            List<String> rightList = new ArrayList<>();
            if (table.get(0) != null) middleList.add(table.get(0));
            else middleList.add(" ");
            rightList.add(" ");
            if (table.get(1) != null && table.get(2) != null) {
                middleList.add("specified");
                rightList.add(table.get(1) + " " + table.get(2));
            }
            else {
                middleList.add("not specified");
                rightList.add(" ");
            }
            if (Boolean.parseBoolean(table.get(3))) {
                middleList.add("issued");
                rightList.add("your registration permit has been issued");
            }
            else {
                middleList.add("not issued");
                rightList.add(" ");
            }
            if (table.get(4) != null) {
                middleList.add("specified");
                rightList.add(table.get(4));
            }
            else {
                middleList.add("not specified");
                rightList.add(" ");
            }
            response.addData("middleList", middleList.toString());
            response.addData("rightList", rightList.toString());
        }
        return response;
    }

    private List<String> getTableData(String username) {
        String path = this.address + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Gson gson = new Gson();
            Student student = gson.fromJson(jo.get("user").toString(), Student.class);
            List<String> tableList = new ArrayList<>();
            tableList.add(student.getStatus().toString());
            tableList.add(student.getSupervisorCode().split(" ")[0]);
            tableList.add(student.getSupervisorCode().split(" ")[1]);
            tableList.add(String.valueOf(student.isRegistrationLicense()));
            tableList.add(student.getRegistrationTime());
            return tableList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUser(String username) {
        String path = this.address + "/user" + username + ".json";
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            Gson gson = new Gson();
            return gson.fromJson(jo.get("user").toString(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response getErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
