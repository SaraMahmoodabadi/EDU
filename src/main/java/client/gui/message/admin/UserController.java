package client.gui.message.admin;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UserController {
    @FXML
    protected Button sendMediaButton;
    @FXML
    protected Button back;
    @FXML
    protected TextArea messageArea;
    @FXML
    protected Button sendButton;
    private String file;
    private String fileFormat;
    private String path;

    @FXML
    void sendMessage(ActionEvent event) {
        if (file == null && messageArea.getText() == null) return;
        if (EDU.isOnline) {
            Request request = new Request(RequestType.SEND_MESSAGE_TO_ADMIN);
            if (file != null) {
                request.addData("file", file);
                request.addData("fileFormat", fileFormat);
            }
            if (messageArea.getText() != null) {
                request.addData("message", messageArea.getText());
            }
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK)
                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            else
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            Message message = new Message();
            message.setSender(EDU.username);
            if (file != null) {
                message.setFile(path);
                message.setFileFormat(fileFormat);
            }
            if (messageArea.getText() != null) {
                message.setMessageText(messageArea.getText());
            }
            writeMessageInFile(message);
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, "offlineMessageRegistered");
        }
        this.path = null;
        this.file = null;
        this.fileFormat = null;
        this.messageArea.clear();
    }

    @FXML
    public void chooseMedia(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            MediaHandler handler = new MediaHandler();
            this.path = file.getAbsolutePath();
            this.file = handler.encode(path);
            int n = path.split("\\.").length;
            this.fileFormat = path.split("\\.")[n-1];
        }
    }

    private void writeMessageInFile(Message message) {
        String path = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "adminMessagesPath");
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        try {
            Object obj = new JSONParser().parse(new FileReader(path));
            JSONObject jo = (JSONObject) obj;
            if (jo == null) {
                jo = new JSONObject();
            }
            JSONArray jsonArray = (JSONArray) jo.get(EDU.username + "Messages");
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            else {
                jo.remove(EDU.username + "Messages");
            }
            jsonArray.add(message);
            jo.put(EDU.username + "Messages", jsonArray);
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(file, jo);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    @FXML
    void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

}
