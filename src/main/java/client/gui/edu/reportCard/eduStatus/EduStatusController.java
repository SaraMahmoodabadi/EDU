package client.gui.edu.reportCard.eduStatus;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
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
    private Request request;
    private boolean stop;

    public void showByCode(ActionEvent actionEvent) {
        if (studentCodeField.getText() != null) {
            request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
            request.addData("studentCode", studentCodeField.getText());
            request.addData("collegeCode", EDU.collegeCode);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) setData(response);
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    public void showByName(ActionEvent actionEvent) {
        if (studentNameField.getText() != null) {
            request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
            request.addData("studentName", studentNameField.getText());
            request.addData("collegeCode", EDU.collegeCode);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) setData(response);
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
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
        request = new Request(RequestType.SHOW_EDU_STATUS_PAGE);
        request.addData("collegeCode", EDU.collegeCode);
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
        numberPassedLabel.setText(String.valueOf(response.getData("numberPassed")));
        averageLabel.setText(String.valueOf(response.getData("average")));
        table.getItems().clear();
        table.getItems().addAll(scores);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    if (!EDU.isOnline) break;
                    Thread.sleep(2000);
                    if (request == null) continue;
                    Platform.runLater(() -> {
                        if (!EDU.isOnline)
                            showOfflineMood();
                        else {
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                List<Score> scores = new ArrayList<>();
                                response.getData().forEach((K, V) -> {
                                    if (K.startsWith("score")) {
                                        scores.add((Score) V);
                                    }
                                });
                                table.getItems().clear();
                                table.getItems().addAll(scores);
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        makeTable();
        if (!EDU.isOnline) showOfflineMood();
        if (EDU.userType == UserType.STUDENT) {
            hide();
            getData();
        }
        updateData();
    }
}
