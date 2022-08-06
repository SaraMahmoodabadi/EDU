package client.gui.message.admin;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class UserController {
    @FXML
    protected Button back;
    @FXML
    protected TextArea messageArea;
    @FXML
    protected Button sendButton;

    @FXML
    void sendMessage(ActionEvent event) {
        if (messageArea.getText() == null) return;
        if (EDU.isOnline) {
            Request request = new Request(RequestType.SEND_MESSAGE_TO_ADMIN);
            request.addData("message", messageArea.getText());
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK)
                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            else
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            Config.getConfig(ConfigType.ADMIN_MESSAGES).write(EDU.username + "Message", messageArea.getText());
        }
    } //TODO : HANDLE OFFLINE MOOD

    @FXML
    void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

}
