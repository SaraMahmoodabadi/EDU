package client.gui.edu.reportCard.temporaryScores.eduAssistant;

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
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TemporaryScoresController implements Initializable {
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
    protected TextField lessonCodeField;
    @FXML
    protected TextField groupField;
    @FXML
    protected TextField studentCodeField;
    @FXML
    protected TextField professorNameField;
    @FXML
    protected Button showLessonScores;
    @FXML
    protected Button showStudentScores;
    @FXML
    protected Button showProfessorScores;
    @FXML
    protected TableView<Score> table;
    @FXML
    protected TableColumn<Score, String> lessonCodeColumn;
    @FXML
    protected TableColumn<Score, String> professorCodeColumn;
    @FXML
    protected TableColumn<Score, String> studentCodeColumn;
    @FXML
    protected TableColumn<Score, String> scoreColumn;
    @FXML
    protected TableColumn<Score, String> protestColumn;
    @FXML
    protected TableColumn<Score, String> protestAnswerColumn;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Rectangle rectangle4;
    @FXML
    protected Text numberPassedText;
    @FXML
    protected Text numberFailedText;
    @FXML
    protected Text averageText;
    @FXML
    protected Text averagePassedText;
    @FXML
    protected Label numberPassedLabel;
    @FXML
    protected Label averageLabel;
    @FXML
    protected Label numberFailedLabel;
    @FXML
    protected Label averagePassedLabel;
    @FXML
    protected Button showPage;
    private boolean stop;
    private Request request;

    public void showLessonScores(ActionEvent actionEvent) {
        if (lessonCodeField.getText() == null || groupField.getText() == null) showNullAlert();
        if (lessonCodeField.getText().equals("") || groupField.getText().equals("")) showNullAlert();
        else {
            request = new Request(RequestType.SHOW_LESSON_SCORES);
            request.addData("lessonCode", lessonCodeField.getText());
            request.addData("group", groupField.getText());
            request.addData("page", "eduAssistant");
            showRequestResult(request);
        }
    }

    public void showStudentScores(ActionEvent actionEvent) {
        if (studentCodeField.getText() == null) showNullAlert();
        if (studentCodeField.getText().equals("")) showNullAlert();
        else {
            request = new Request(RequestType.SHOW_STUDENT_SCORES);
            request.addData("studentCode", studentCodeField.getText());
            request.addData("collegeCode", EDU.collegeCode);
            showRequestResult(request);
        }
    }

    public void showProfessorScores(ActionEvent actionEvent) {
        if (professorNameField.getText() == null) showNullAlert();
        if (professorNameField.getText().equals("")) showNullAlert();
        else {
            request = new Request(RequestType.SHOW_PROFESSOR_SCORES);
            request.addData("professorName", professorNameField.getText());
            showRequestResult(request);
        }
    }

    public void showPage(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "professorTemporaryScoresPage");
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

    private void makeTable() {
        lessonCodeColumn.setCellValueFactory(new PropertyValueFactory<>("lessonCode"));
        professorCodeColumn.setCellValueFactory(new PropertyValueFactory<>("professorCode"));
        studentCodeColumn.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        protestColumn.setCellValueFactory(new PropertyValueFactory<>("protest"));
        protestAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("protestAnswer"));
    }

    private void showNullAlert() {
        String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullItem");
        AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
    }

    private void hideLessonSummary() {
        rectangle1.setVisible(false);
        rectangle2.setVisible(false);
        rectangle3.setVisible(false);
        rectangle4.setVisible(false);
        averageText.setOpacity(0);
        averageLabel.setOpacity(0);
        averagePassedText.setOpacity(0);
        averagePassedLabel.setOpacity(0);
        numberPassedText.setOpacity(0);
        numberPassedLabel.setOpacity(0);
        numberFailedText.setOpacity(0);
        numberFailedLabel.setOpacity(0);
    }

    private void showLessonSummary(Response response) {
        rectangle1.setVisible(true);
        rectangle2.setVisible(true);
        rectangle3.setVisible(true);
        rectangle4.setVisible(true);
        averageText.setOpacity(1);
        averageLabel.setOpacity(1);
        averagePassedText.setOpacity(1);
        averagePassedLabel.setOpacity(1);
        numberPassedText.setOpacity(1);
        numberPassedLabel.setOpacity(1);
        numberFailedText.setOpacity(1);
        numberFailedLabel.setOpacity(1);
        averageLabel.setText(String.valueOf(response.getData("average")));
        averagePassedLabel.setText(String.valueOf(response.getData("averagePassed")));
        numberPassedLabel.setText(String.valueOf(response.getData("numberPassed")));
        numberFailedLabel.setText(String.valueOf(response.getData("numberFailed")));
    }

    private void showRequestResult(Request request) {
        request.addData("collegeCode", EDU.collegeCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            hideLessonSummary();
            List<Score> scores = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("score")) {
                    scores.add((Score) V);
                }
            });
            updateTable(scores);
            if (request.getRequestType() == RequestType.SHOW_LESSON_SCORES) {
                showLessonSummary(response);
            }
        }
    }

    private void updateTable(List<Score> scores) {
        table.getItems().clear();
        table.getItems().addAll(scores);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (request != null) {
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                List<Score> scores = new ArrayList<>();
                                response.getData().forEach((K, V) -> {
                                    if (K.startsWith("score")) {
                                        scores.add((Score) V);
                                    }
                                });
                                updateTable(scores);
                            }
                        }
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
        makeTable();
        hideLessonSummary();
        updateData();
    }
}
