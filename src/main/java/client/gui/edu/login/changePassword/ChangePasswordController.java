package client.gui.edu.login.changePassword;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
