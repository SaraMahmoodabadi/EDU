package client.gui.edu.registration.newUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class NewUserController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Text chooseText;
    @FXML
    protected RadioButton student;
    @FXML
    protected RadioButton professor;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Text firstNameText;
    @FXML
    protected TextField firstNameField;
    @FXML
    protected Text lastNameText;
    @FXML
    protected TextField lastNameField;
    @FXML
    protected Text nationalCodeText;
    @FXML
    protected TextField nationalCodeField;
    @FXML
    protected Text emailText;
    @FXML
    protected TextField emailField;
    @FXML
    protected Text phoneText;
    @FXML
    protected TextField phoneField;
    @FXML
    protected Text usernameText;
    @FXML
    protected TextField usernameField;
    @FXML
    protected Text passwordText;
    @FXML
    protected TextField passwordField;
    @FXML
    protected ImageView profileImage;
    @FXML
    protected Button select;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Text studentCodeText;
    @FXML
    protected TextField studentCodeField;
    @FXML
    protected Text supervisorCodeText;
    @FXML
    protected TextField superVisorCodeField;
    @FXML
    protected Text gradeText1;
    @FXML
    protected RadioButton undergraduate;
    @FXML
    protected RadioButton master;
    @FXML
    protected RadioButton doctorate;
    @FXML
    protected Rectangle rectangle4;
    @FXML
    protected Text professorCodeText;
    @FXML
    protected TextField professorCodeField;
    @FXML
    protected Text roomText;
    @FXML
    protected TextField roomField;
    @FXML
    protected Text gradeText2;
    @FXML
    protected RadioButton assistant;
    @FXML
    protected RadioButton associate;
    @FXML
    protected RadioButton full;
    @FXML
    protected Button register;
    @FXML
    protected Text enteringYearText;
    @FXML
    protected TextField EnteringYearField;

    public void select(ActionEvent actionEvent) {
    }

    public void register(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
