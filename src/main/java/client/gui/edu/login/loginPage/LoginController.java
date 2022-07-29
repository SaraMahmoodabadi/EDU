package client.gui.edu.login.loginPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import client.gui.EDU;
import client.gui.AlertMonitor;

import shared.model.user.UserType;
import shared.model.user.professor.Type;
import shared.model.user.student.EducationalStatus;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    //color 1 : #b151b8
    //color 2 : #ffd100

    @FXML
    protected Rectangle leftRectangle;
    @FXML
    protected Rectangle rightRectangle;
    @FXML
    protected Rectangle upRectangle;
    @FXML
    protected Rectangle middleRectangle;
    @FXML
    protected TextField username;
    @FXML
    protected TextField password;
    @FXML
    protected TextField captchaText;
    @FXML
    protected ImageView sharifImage;
    @FXML
    protected ImageView captchaImage;
    @FXML
    protected ImageView recaptchaImage;
    @FXML
    protected Button recaptcha;
    @FXML
    protected Button login;
    @FXML
    protected Text startText;
    @FXML
    protected Circle person;


    public void recaptcha(ActionEvent actionEvent) {
        Request request = new Request(RequestType.START_CONNECTION);
        Response response = EDU.serverController.sendRequest(request);
        Object image = response.getData("captcha image");
        this.captchaImage.setImage(new ImageHandler().getImage(String.valueOf(image)));
    }

    public void login(ActionEvent actionEvent) {
        if (isNull()) return;
        Request request = new Request(RequestType.LOGIN);
        request.addData("username", this.username.getText());
        request.addData("password", this.password.getText());
        request.addData("captcha", this.captchaText.getText());
        Response response = EDU.serverController.sendRequest(request);
        changeScene(actionEvent, response);
    }

    //TODO : Change scene for other type of users
    private void changeScene(ActionEvent actionEvent, Response response) {
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            EDU.userType = (UserType) response.getData("userType");
            if (EDU.userType == UserType.STUDENT || EDU.userType == UserType.PROFESSOR) {
                EDU.collegeCode = (String) response.getData("collegeCode");
            }
            if (EDU.userType == UserType.STUDENT) {
               checkWithdrawal(response);
            }
            if (EDU.userType == UserType.PROFESSOR) {
                EDU.professorType = (Type) response.getData("professorType");
            }
            if (response.getNotificationMessage().equals
                    (Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "changePassword"))) {
                EDU.sceneSwitcher.switchScene(actionEvent, "changePasswordPage");
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
    }

    private void checkWithdrawal(Response response) {
        EducationalStatus eduStatus = (EducationalStatus) response.getData("eduStatus");
        if (eduStatus == EducationalStatus.WITHDRAWAL_FROM_EDUCATION) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).
                    getProperty(String.class, "withdrawalError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
        }
    }

    private boolean isNull() {
        if (this.username.getText() == null ||
                this.password.getText() == null ||
                this.captchaText.getText() == null) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).
                    getProperty(String.class, "nullFieldsError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
            return true;
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Response response = EDU.serverController.sendRequest(new Request(RequestType.START_CONNECTION));
        Object image = response.getData("captchaImage");
        this.captchaImage.setImage(new ImageHandler().getImage(String.valueOf(image)));
    }
}
