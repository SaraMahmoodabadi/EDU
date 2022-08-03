package client.gui.edu.eduServices.request.professor;

import client.gui.AlertMonitor;
import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfessorRequestController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Label recommendationLabel;
    @FXML
    protected Label passedLessonsLabel;
    @FXML
    protected Label scoresLabel;
    @FXML
    protected Label assistantLabel;
    @FXML
    protected TextField studentCode;
    @FXML
    protected TextField passedLessonsField;
    @FXML
    protected TextField scoresField;
    @FXML
    protected TextField assistantField;
    @FXML
    protected ComboBox<String> requestBox;
    @FXML
    protected RadioButton accept;
    @FXML
    protected RadioButton reject;
    @FXML
    protected Button registerRecommendation;
    @FXML
    protected Button register;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    private ToggleGroup result;

    public void registerRecommendation(ActionEvent actionEvent) {
        if (isNullRecommendation()) showNullAlert("nullItem");
        else {
            Request request = new Request(RequestType.REGISTER_RECOMMENDATION);
            request.addData("studentCode", studentCode.getText());
            request.addData("firstBlank", passedLessonsField.getText());
            request.addData("secondBlank", scoresField.getText());
            request.addData("thirdBlank", assistantField.getText());
            showRequestResult(request);
        }
    }

    public void register(ActionEvent actionEvent) {
        if (isNullRequest()) showNullAlert("nullError");
        else {
            Request request = new Request(RequestType.REGISTER_REQUEST_ANSWER);
            request.addData("studentCode", studentCode.getText());
            request.addData("type", requestBox.getValue());
            boolean requestResult = result.getSelectedToggle() == accept;
            request.addData("result", requestResult);
            showRequestResult(request);
        }
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private boolean isNullRecommendation() {
        if (studentCode.getText() == null) return true;
        return passedLessonsField.getText() == null &&
                scoresField.getText() == null &&
                assistantField.getText() == null;
    }

    private boolean isNullRequest() {
        return studentCode.getText() == null ||
                requestBox.getValue() == null ||
                result.getSelectedToggle() == null;
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

    private void showNullAlert(String errorName) {
        String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, errorName);
        AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
    }

    private void makeBox() {
        if (EDU.professorType == Type.EDUCATIONAL_ASSISTANT) {
            requestBox.getItems().add(shared.model.message.request.Type.WITHDRAWAL.toString());
            requestBox.getItems().add(shared.model.message.request.Type.MINOR.toString());
        }
    }

    private void setGroup() {
        result = new ToggleGroup();
        accept.setToggleGroup(result);
        reject.setToggleGroup(result);
    }

    private void hide() {
        rectangle2.setVisible(false);
        requestBox.setVisible(false);
        requestBox.setDisable(true);
        accept.setVisible(false);
        reject.setVisible(false);
        register.setVisible(false);
        register.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeBox();
        setGroup();
        if (EDU.professorType != Type.EDUCATIONAL_ASSISTANT) hide();
    }
}
