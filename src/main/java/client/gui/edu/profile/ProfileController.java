package client.gui.edu.profile;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.user.UserType;
import shared.model.user.professor.Professor;
import shared.model.user.student.Student;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.ImageHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
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
    protected TextField phoneNumberField;
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
    private boolean stop;


    public void registerEmail(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REGISTER_EMAIL);
        request.addData("email", emailField.getText());
        showRequestResult(request);
    }

    public void registerPhoneNumber(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REGISTER_PHONE_NUMBER);
        request.addData("phoneNumber", phoneNumberField.getText());
        showRequestResult(request);
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
        if (EDU.userType == UserType.MR_MOHSENI)
            EDU.sceneSwitcher.switchScene(actionEvent, "mohseni");
        else
            EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
    }

    private void makeFields() {
        if (EDU.userType == UserType.PROFESSOR) {
            userCodeText.setText("professor code:");
            averageOrRoomText.setText("room number:");
            supervisorText.setVisible(false);
            enteringYearText.setVisible(false);
            statusText.setVisible(false);
        }
    }

    private void getStudentData() {
        Request request = new Request(RequestType.SHOW_PROFILE_PAGE, UserType.STUDENT);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            Student student = (Student) response.getData("student");
            setStudentData(student);
            Object image = response.getData("profile");
            this.profilePicture.setImage(new ImageHandler().getImage(String.valueOf(image)));
        }
    }

    private void setStudentData(Student student) {
        firstNameLabel.setText(student.getFirstName());
        lastNameLabel.setText(student.getLastName());
        nationalCodeLabel.setText(String.valueOf(student.getNationalCode()));
        userCodeLabel.setText(student.getStudentCode());
        collegeLabel.setText(student.getCollegeCode());
        emailField.setText(student.getEmailAddress());
        phoneNumberField.setText(String.valueOf(student.getPhoneNumber()));
        averageOrRoomLabel.setText(String.valueOf(student.getRate()));
        degreeLabel.setText(String.valueOf(student.getGrade()));
        supervisorLabel.setText(student.getSupervisorCode());
        enteringYearLabel.setText(String.valueOf(student.getEnteringYear()));
        statusLabel.setText(String.valueOf(student.getStatus()));
    }

    private void getProfessorData() {
        Request request = new Request(RequestType.SHOW_PROFILE_PAGE, UserType.PROFESSOR);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            Professor professor = (Professor) response.getData("professor");
            firstNameLabel.setText(professor.getFirstName());
            lastNameLabel.setText(professor.getLastName());
            nationalCodeLabel.setText(String.valueOf(professor.getNationalCode()));
            userCodeLabel.setText(professor.getProfessorCode());
            collegeLabel.setText(professor.getCollegeCode());
            emailField.setText(professor.getEmailAddress());
            phoneNumberField.setText(String.valueOf(professor.getPhoneNumber()));
            averageOrRoomLabel.setText(String.valueOf(professor.getRoomNumber()));
            degreeLabel.setText(String.valueOf(professor.getDegree()));
            Object image = response.getData("profile");
            this.profilePicture.setImage(new ImageHandler().getImage(String.valueOf(image)));
        }
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

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    if (!EDU.isOnline) break;
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline) showOfflineMood();
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        makeFields();
        if (EDU.userType == UserType.STUDENT) getStudentData();
        else if (EDU.userType == UserType.PROFESSOR) getProfessorData();
        else if (EDU.userType == UserType.MR_MOHSENI) {
            Response response = EDU.serverController.sendRequest(ServerController.request);
            if (response.getStatus() == ResponseStatus.OK) {
                Student student = (Student) response.getData("student");
                setStudentData(student);
                Object image = response.getData("profile");
                this.profilePicture.setImage(new ImageHandler().getImage(String.valueOf(image)));
                registerButton1.setDisable(true);
                registerButton1.setVisible(false);
                registerButton2.setVisible(false);
                registerButton2.setDisable(true);
                emailField.setEditable(false);
                phoneNumberField.setEditable(false);
            }
        }
        updateData();
    }
}
