package client.gui.edu.login.loginPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    }

    public void login(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
