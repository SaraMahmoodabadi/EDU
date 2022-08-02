package client.gui.edu.login.changePassword;

import client.gui.EDU;
import client.gui.AlertMonitor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    @FXML
    protected Pane pane;
    @FXML
    protected Circle bigCircle;
    @FXML
    protected Circle smallCircle;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle middleRectangle;
    @FXML
    protected Label text;
    @FXML
    protected TextField previousPassword;
    @FXML
    protected TextField newPassword;
    @FXML
    protected Button register;


    public void register(ActionEvent actionEvent) {
        if (isNull()) return;
        Request request = new Request(RequestType.CHANGE_PASSWORD);
        request.addData("previousPassword", this.previousPassword.getText());
        request.addData("newPassword", this.newPassword.getText());
        Response response = EDU.serverController.sendRequest(request);
        changeScene(actionEvent, response);
    }

    //TODO : Change scene for other type of users
    private void changeScene(ActionEvent actionEvent, Response response) {
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            if (EDU.userType == UserType.EDU_ADMIN) {

            }
            else if(EDU.userType == UserType.MR_MOHSENI) {

            }
            else {
                EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
            }
        }
    }

    private boolean isNull() {
        if (this.previousPassword.getText() == null ||
                this.newPassword.getText() == null) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).
                    getProperty(String.class, "nullFieldsError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
            return true;
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
