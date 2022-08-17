package client.gui.edu.reportCard.temporaryScores.professor;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.university.lesson.Lesson;
import shared.model.university.lesson.score.Score;
import shared.model.user.UserType;
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
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Text explanationText;
    @FXML
    protected TextField lessonCodeField;
    @FXML
    protected TextField groupField;
    @FXML
    protected TextField scoreField;
    @FXML
    protected TextArea protestAnswerArea;
    @FXML
    protected TableView<Score> table;
    @FXML
    protected TableColumn<Score, String> studentCodeColumn;
    @FXML
    protected TableColumn<Score, String> scoreColumn;
    @FXML
    protected TableColumn<Score, String> protestColumn;
    @FXML
    protected TableColumn<Score, String> protestAnswerColumn;
    @FXML
    protected Button show;
    @FXML
    protected Button registerScore;
    @FXML
    protected Button registerProtestAnswer;
    @FXML
    protected Button registerAllScores;
    @FXML
    protected Button finalizeScores;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    private List<Score> scores;
    private boolean registerResult = false;
    private boolean stop;
    private Request request;
    private String lessonCode;
    private boolean hadScore = false;
    private Score score;
    //TODO: to ping change updatedScores list (just get protest)

    public void show(ActionEvent actionEvent) {
        if (checkNullItems()) return;
        request = new Request(RequestType.SHOW_LESSON_SCORES, UserType.PROFESSOR);
        request.addData("lessonCode", lessonCodeField.getText());
        request.addData("group", groupField.getText());
        request.addData("page", "professor");
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            registerResult = false;
            lessonCode = lessonCodeField.getText();
            scores = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("score")) {
                    scores.add((Score) V);
                }
            });
            updateTable(scores);
            hadScore = hadScore();
        }
    }

    public void select(MouseEvent mouseEvent) {
        score = table.getSelectionModel().getSelectedItem();
    }

    public void registerScore(ActionEvent actionEvent) {
        if (score == null) {
            return;
        }
        Score newScore = score;
        if (scoreField.getText() != null) {
            Double score;
            try {
                score = Double.parseDouble(scoreField.getText());
            }
            catch (Exception e) {
                String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "numberError");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
                return;
            }
            if (score > 20 || score < 0) {
                String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "invalidScore");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
                return;
            }
            scores.remove(newScore);
            newScore.setScore(String.valueOf(score));
            scores.add(newScore);
            updateTable(scores);
            if (hadScore) {
                newScore.setLessonGroup(Integer.parseInt(groupField.getText()));
                newScore.setLessonCode(lessonCode);
                request = new Request(RequestType.REGISTER_SCORE);
                request.addData("score" , newScore);
                showRequestResult(request);
            }
        }
    }

    public void registerProtestAnswer(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (score.getProtest() == null) {
            return;
        }
        if (protestAnswerArea.getText() != null) {
            request = new Request(RequestType.REGISTER_PROTEST_ANSWER);
            request.addData("score", score);
            request.addData("protestAnswer", protestAnswerArea.getText());
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                scores.remove(score);
                score.setProtestAnswer(protestAnswerArea.getText());
                scores.add(score);
                updateTable(scores);
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
            else {
                if (request.getRequestType() == RequestType.REGISTER_ALL_SCORES) registerResult = true;
                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            }
        }
    }

    public void registerAllScores(ActionEvent actionEvent) {
        if (hadScore) return;
        request = new Request(RequestType.REGISTER_ALL_SCORES);
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getScore() == null || scores.get(i).getScore().equals("")) {
                String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullScores");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
                return;
            }
            scores.get(i).setLessonGroup(Integer.parseInt(groupField.getText()));
            scores.get(i).setLessonCode(lessonCode);
            request.addData("score" + i, scores.get(i));
        }
        showRequestResult(request);
    }

    public void finalizeScores(ActionEvent actionEvent) {
        if (checkNullItems()) return;
        if (!hadScore) {
           if (!registerResult) {
               String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "finalizeScores");
               AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
               return;
           }
        }
        Request request = new Request(RequestType.FINALIZE_SCORES);
        request.addData("lessonCode", lessonCode);
        request.addData("group", groupField.getText());
        for (int i = 0; i < scores.size(); i++) {
            request.addData("score" + i, scores.get(i));
        }
        showRequestResult(request);
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

    private void showRequestResult(Request request) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            if (request.getRequestType() == RequestType.REGISTER_ALL_SCORES) registerResult = true;
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
    }

    private void makeTable() {
        studentCodeColumn.setCellValueFactory(new PropertyValueFactory<>("studentCode"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        protestColumn.setCellValueFactory(new PropertyValueFactory<>("protest"));
        protestAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("protestAnswer"));
    }

    private void updateTable(List<Score> scores) {
        table.getItems().clear();
        table.getItems().addAll(scores);
    }

    private boolean checkNullItems() {
        if (lessonCodeField.getText() == null || groupField.getText() == null) {
            String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullItem");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
            return true;
        }
        return false;
    }

    private boolean hadScore() {
        for (Score score : scores) {
            if (score.getScore() == null) return false;
            else if (score.getScore().equals("")) return false;
        }
        return true;
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (lessonCode != null && !lessonCode.equals("") &&
                                groupField.getText() != null && !groupField.getText().equals("")) {
                            Request request = new Request(RequestType.SHOW_LESSON_SCORES, UserType.PROFESSOR);
                            request.addData("lessonCode", lessonCodeField.getText());
                            request.addData("group", groupField.getText());
                            request.addData("page", "professor");
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                List<Score> scores = new ArrayList<>();
                                response.getData().forEach((K, V) -> {
                                    if (K.startsWith("score")) {
                                        scores.add((Score) V);
                                    }
                                });
                                for (Score score : scores) {
                                    if (score.getScore() != null && !score.getScore().equals("")) {
                                        updateTable(scores);
                                    }
                                    break;
                                }
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
        updateData();
    }
}
