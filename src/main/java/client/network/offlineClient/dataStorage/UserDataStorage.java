package client.network.offlineClient.dataStorage;

import client.gui.EDU;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.response.Response;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserDataStorage {
    private final String address;

    public UserDataStorage() {
        this.address = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "users");
    }

    public void storeData(Response response) {
        if (response.getData() == null) return;
        File file = makeFile();
        if (file == null) return;
        String fileFormat = (String) response.getData("fileFormat");
        String profile = (String) response.getData("profile");
        String path = saveFile(profile, fileFormat);
        JSONObject jsonObject = new JSONObject();
        if (EDU.userType == UserType.STUDENT) {
            Student student = (Student) response.getData("student");
            student.setImageAddress(path);
            jsonObject.put("user", student);
        }
        else if (EDU.userType == UserType.PROFESSOR){
            Professor professor = (Professor) response.getData("professor");
            professor.setImageAddress(path);
            jsonObject.put("user", professor);
        }
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

    private String saveFile(String image, String fileFormat) {
        String path = Config.getConfig(ConfigType.CLIENT_DATA).getProperty(String.class, "profileImages");
        MediaHandler handler = new MediaHandler();
        path = path + "/profile" + EDU.username + "." + fileFormat;
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.writeBytesToFile(path, handler.decode(image));
        return path;
    }
}
