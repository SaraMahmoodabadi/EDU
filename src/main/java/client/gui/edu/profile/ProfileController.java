package client.gui.edu.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected ImageView profilePicture;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Text firstNameText;
    @FXML
    protected Text lastNameText;
    @FXML
    protected Text nationalCodeText;
    @FXML
    protected Text userCodeText;
    @FXML
    protected Label firstNameLabel;
    @FXML
    protected Label nationalCodeLabel;
    @FXML
    protected Label lastNameLabel;
    @FXML
    protected Label userCodeLabel;
    @FXML
    protected Text collegeText;
    @FXML
    protected Text emailText;
    @FXML
    protected Text phoneNumberText;
    @FXML
    protected Text averageOrRoomText;
    @FXML
    protected Text degreeText;
    @FXML
    protected Text supervisorText;
    @FXML
    protected Text enteringYearText;
    @FXML
    protected Text statusText;
    @FXML
    protected Label collegeLabel;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField phoneNumberLabel;
    @FXML
    protected Button registerButton1;
    @FXML
    protected ImageView registerImage1;
    @FXML
    protected Button registerButton2;
    @FXML
    protected ImageView registerImage2;
    @FXML
    protected Label averageOrRoomLabel;
    @FXML
    protected Label degreeLabel;
    @FXML
    protected Label supervisorLabel;
    @FXML
    protected Label enteringYearLabel;
    @FXML
    protected Label statusLabel;


    public void registerEmail(ActionEvent actionEvent) {
    }

    public void registerPhoneNumber(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
