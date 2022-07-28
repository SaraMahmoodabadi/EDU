package client.gui.edu.reportCard.eduStatus;

import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.university.lesson.score.Score;
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EduStatusController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected TableView<Score> table;
    @FXML
    protected TableColumn<Score, String> studentCodeColumn;
    @FXML
    protected TableColumn<Score, String> lessonCodeColumn;
    @FXML
    protected TableColumn<Score, String> scoreColumn;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Text numberPassedText;
    @FXML
    protected Label numberPassedLabel;
    @FXML
    protected Rectangle rectangle4;
    @FXML
    protected Text averageText;
    @FXML
    protected Label averageLabel;
    @FXML
    protected TextField studentCodeField;
    @FXML
    protected TextField studentNameField;
    @FXML
    protected Button showByName;
    @FXML
    protected Button showByCode;

    public void showByCode(ActionEvent actionEvent) {
        if (studentCodeField.getText() != null) {
            Request request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
            request.addData("studentCode", studentCodeField.getText());
            request.addData("collegeCode", EDU.collegeCode);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) setData(response);
        }
    }

    public void showByName(ActionEvent actionEvent) {
        if (studentNameField.getText() != null) {
            Request request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
            request.addData("studentName", studentNameField.getText());
            request.addData("collegeCode", EDU.collegeCode);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) setData(response);
        }
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void hide() {
        studentNameField.setVisible(false);
        studentCodeField.setVisible(false);
        showByCode.setVisible(false);
        showByName.setVisible(false);
    }

    private void makeTable() {
        studentCodeColumn.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
        lessonCodeColumn.setCellValueFactory(new PropertyValueFactory<>("lessonCode"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    private void getData() {
        Request request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) setData(response);
    }

    private void setData(Response response) {
        List<Score> scores = new ArrayList<>();
        response.getData().forEach((K, V) -> {
            if (K.startsWith("score")) {
                scores.add((Score) V);
            }
        });
        numberPassedLabel.setText((String) response.getData("numberPassed"));
        averageLabel.setText((String) response.getData("average"));
        table.getItems().clear();
        table.getItems().addAll(scores);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTable();
        if (EDU.userType == UserType.STUDENT) {
            hide();
            getData();
        }
    }
}
