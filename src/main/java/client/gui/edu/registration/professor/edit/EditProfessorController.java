package client.gui.edu.registration.professor.edit;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.user.professor.MasterDegree;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ResourceBundle;

public class EditProfessorController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected Button add;
    @FXML
    protected TextField professorCode;
    @FXML
    protected Button deposal;
    @FXML
    protected Button appointment;
    @FXML
    protected Text phoneText;
    @FXML
    protected TextField phoneField;
    @FXML
    protected Text emailText;
    @FXML
    protected TextField emailField;
    @FXML
    protected Text roomText;
    @FXML
    protected TextField roomField;
    @FXML
    protected Text gradeText;
    @FXML
    protected RadioButton assistant;
    @FXML
    protected RadioButton associate;
    @FXML
    protected RadioButton full;
    @FXML
    protected Button edit;
    @FXML
    protected Button remove;
    private final ToggleGroup grade = new ToggleGroup();
    private boolean stop;

    public void add(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "newUserPage");
    }

    public void deposal(ActionEvent actionEvent) {
        if (professorCode.getText() == null) return;
        Request request = new Request(RequestType.DEPOSAL_EDU_ASSISTANT);
        request.addData("professorCode", professorCode.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void appointment(ActionEvent actionEvent) {
        if (professorCode.getText() == null) return;
        Request request = new Request(RequestType.APPOINTMENT_EDU_ASSISTANT);
        request.addData("professorCode", professorCode.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void edit(ActionEvent actionEvent) {
        if (professorCode.getText() == null) return;
        Request request = new Request(RequestType.EDIT_PROFESSOR);
        request.addData("professorCode", professorCode.getText());
        request.addData("phoneNumber", phoneField.getText());
        request.addData("email", emailField.getText());
        request.addData("room", roomField.getText());
        request.addData("degree", setDegree());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void remove(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REMOVE_PROFESSOR);
        request.addData("professorCode", professorCode.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "professorListPage");
    }

    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
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

    protected MasterDegree setDegree() {
        if (grade.getSelectedToggle().equals(assistant))
            return MasterDegree.ASSISTANT_PROFESSOR;
        else if (grade.getSelectedToggle().equals(associate))
            return MasterDegree.ASSOCIATE_PROFESSOR;
        else if (grade.getSelectedToggle().equals(full))
            return MasterDegree.FULL_PROFESSOR;
        else return null;
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline) showOfflineMood();
                    });
                } catch (InterruptedException ignored) {}
                if (!EDU.isOnline) break;
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        assistant.setToggleGroup(grade);
        associate.setToggleGroup(grade);
        full.setToggleGroup(grade);
        updateData();
    }
}
