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
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.File;

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
            if (file != null) {
                Config.getConfig(ConfigType.ADMIN_MESSAGES).write(EDU.username + "Message", this.path);
            }
            if (messageArea.getText() != null) {
                Config.getConfig(ConfigType.ADMIN_MESSAGES).write(EDU.username + "Message", messageArea.getText());
            }
        }
    } //TODO : HANDLE OFFLINE MOOD

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

    @FXML
    void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

}
