package client.gui.edu.registration.newUser;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shared.model.user.UserType;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.model.user.student.EducationalStatus;
import shared.model.user.student.Grade;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;

import javax.rmi.CORBA.Stub;
import java.io.File;
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
    @FXML
    protected Text status;
    @FXML
    protected RadioButton studying;
    @FXML
    protected RadioButton graduated;
    @FXML
    protected RadioButton withdrawal;
    private ToggleGroup type;
    private ToggleGroup sDegree;
    private ToggleGroup pDegree;
    private ToggleGroup eduStatus;
    private String imageCode;

    public void select(ActionEvent actionEvent) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("select image");
        imageChooser.setInitialDirectory(new File(imageCode));
        File file = imageChooser.showOpenDialog(ServerController.edu);
        if(file != null) {
            Image image = new Image(file.toURI().toString());
            profileImage.setImage(image);
            imageCode = file.toURI().toString();
        }
    }

    public void register(ActionEvent actionEvent) {
        if (isNull()) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
        }
        else if(isValid(nationalCodeField.getText()) &&
                isValid(EnteringYearField.getText()) &&
                isValid(roomField.getText())) {
            if (student.isSelected()) {
                makeStudent();
            } else if (professor.isSelected()) {
                makeProfessor();
            }
        }
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    public void selectStudent(ActionEvent actionEvent) {
        professorCodeField.setDisable(true);
        assistant.setDisable(true);
        associate.setDisable(true);
        full.setDisable(true);
        roomField.setDisable(true);

        studentCodeField.setDisable(false);
        undergraduate.setDisable(false);
        master.setDisable(false);
        doctorate.setDisable(false);
        superVisorCodeField.setDisable(false);
        studying.setDisable(false);
        graduated.setDisable(false);
        withdrawal.setDisable(false);
    }

    public void selectProfessor(ActionEvent actionEvent) {
        professorCodeField.setDisable(false);
        assistant.setDisable(false);
        associate.setDisable(false);
        full.setDisable(false);
        roomField.setDisable(false);

        studentCodeField.setDisable(true);
        undergraduate.setDisable(true);
        master.setDisable(true);
        doctorate.setDisable(true);
        superVisorCodeField.setDisable(true);
        studying.setDisable(true);
        graduated.setDisable(true);
        withdrawal.setDisable(true);
    }

    private boolean isNull() {
        if (type.getSelectedToggle() == null ||
                firstNameField.getText() == null ||
                lastNameField.getText() == null ||
                nationalCodeField.getText() == null ||
                EnteringYearField.getText() == null ||
                phoneField.getText() == null ||
                emailField.getText() == null ||
                usernameField.getText() == null ||
                passwordField.getText() == null) return true;
        if (student.isSelected()) {
            return studentCodeField.getText() == null ||
                    superVisorCodeField.getText() == null ||
                    sDegree.getSelectedToggle() == null ||
                    eduStatus.getSelectedToggle() == null;
        }
        if (professor.isSelected()) {
            return professorCodeField.getText() == null ||
                    roomField.getText() == null ||
                    pDegree.getSelectedToggle() == null;
        }
        return false;
    }

    private void setToggleGroups() {
        type = new ToggleGroup();
        student.setToggleGroup(type);
        professor.setToggleGroup(type);

        sDegree = new ToggleGroup();
        undergraduate.setToggleGroup(sDegree);
        master.setToggleGroup(sDegree);
        doctorate.setToggleGroup(sDegree);

        pDegree = new ToggleGroup();
        assistant.setToggleGroup(pDegree);
        associate.setToggleGroup(pDegree);
        full.setToggleGroup(pDegree);

        eduStatus = new ToggleGroup();
        studying.setToggleGroup(eduStatus);
        graduated.setToggleGroup(eduStatus);
        withdrawal.setToggleGroup(eduStatus);
    }

    private void makeDisable() {
        if(EDU.professorType.equals(Type.DEAN)) {
            professor.setSelected(true);
            student.setDisable(true);
            type.selectToggle(professor);
            selectProfessor(new ActionEvent());
        }
    }

    private MasterDegree setProfessorDegree() {
        if (pDegree.getSelectedToggle().equals(assistant))
            return MasterDegree.ASSISTANT_PROFESSOR;
        else if (pDegree.getSelectedToggle().equals(associate))
            return MasterDegree.ASSOCIATE_PROFESSOR;
        else
            return MasterDegree.FULL_PROFESSOR;
    }

    private Grade setStudentDegree() {
        if (sDegree.getSelectedToggle().equals(undergraduate))
            return Grade.UNDERGRADUATE;
        else if (sDegree.getSelectedToggle().equals(master))
            return Grade.MASTER;
        else
            return Grade.PHD;
    }

    private EducationalStatus setStatus() {
        if (eduStatus.getSelectedToggle().equals(studying))
            return EducationalStatus.STUDYING;
        else if (eduStatus.getSelectedToggle().equals(graduated))
            return EducationalStatus.GRADUATED;
        else
            return EducationalStatus.WITHDRAWAL_FROM_EDUCATION;
    }

    private boolean isValid(String number) {
        if (number == null) return true;
        try {
            int n = Integer.parseInt(number);
            if (n >= 0) return true;
        } catch (NumberFormatException e) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "numberError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
        }
        return false;
    }

    private void makeStudent() {
        Student student = new Student(firstNameField.getText(), lastNameField.getText(),
                Long.parseLong(nationalCodeField.getText()), EDU.collegeCode,
                usernameField.getText(), passwordField.getText(), UserType.STUDENT,
                emailField.getText(), phoneField.getText(),
                studentCodeField.getText(), Integer.parseInt(EnteringYearField.getText()),
                superVisorCodeField.getText(), setStatus(), setStudentDegree());
        Request request = new Request(RequestType.REGISTER_NEW_USER);
        request.addData("userType", UserType.STUDENT);
        request.addData("student", student);
        addImageToRequest(request);
    }

    private void makeProfessor() {
        Professor professor = new Professor(firstNameField.getText(), lastNameField.getText(),
                Long.parseLong(nationalCodeField.getText()), EDU.collegeCode,
                usernameField.getText(), passwordField.getText(), UserType.PROFESSOR,
                emailField.getText(), phoneField.getText(),
                professorCodeField.getText(), Integer.parseInt(roomField.getText()),
                setProfessorDegree(), Type.PROFESSOR);
        Request request = new Request(RequestType.REGISTER_NEW_USER);
        request.addData("userType", UserType.PROFESSOR);
        request.addData("professor", professor);
        addImageToRequest(request);
    }

    private void addImageToRequest(Request request) {
        String image = new ImageHandler().encode(imageCode);
        if (image != null) {
            request.addData("profile", image);
        }
        showRequestResult(request);
    }

    private void showRequestResult(Request request) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageCode = Config.getConfig(ConfigType.CLIENT_IMAGE).getProperty(String.class, "defaultProfile");
        setToggleGroups();
        makeDisable();
    }
}
