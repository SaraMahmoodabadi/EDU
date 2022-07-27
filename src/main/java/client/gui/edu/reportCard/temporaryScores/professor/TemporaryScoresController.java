package client.gui.edu.reportCard.temporaryScores.professor;

import client.gui.AlertMonitor;
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
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TemporaryScoresController implements Initializable {

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
    private List<Score> updatedScores;
    private boolean registerResult = false;
    //TODO: to ping change updatedScores list (just get protest)

    public void show(ActionEvent actionEvent) {
        if (checkNullItems()) return;
        Request request = new Request(RequestType.SHOW_TEMPORARY_SCORES_PAGE, UserType.PROFESSOR);
        request.addData("lessonCode", lessonCodeField.getText());
        Response response = EDU.serverController.getResponse();
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            scores = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("score")) {
                    scores.add((Score) V);
                }
            });
            updatedScores = scores;
            updateTable(scores);
        }
    }

    public void registerScore(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        Score newScore = table.getSelectionModel().getSelectedItem();
        if (scoreField.getText() != null) {
            Double score;
            try {
                score = Double.parseDouble(scoreField.getText());
            }
            catch (Exception e) {
                String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty("numberError");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
                return;
            }
            if (score > 20 || score < 0) {
                String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty("invalidScore");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
                return;
            }
            updatedScores.remove(newScore);
            newScore.setScore(String.valueOf(score));
            updatedScores.add(newScore);
            updateTable(updatedScores);
            if (hadScore()) {
                Request request = new Request(RequestType.REGISTER_ALL_SCORES);
                request.addData("score" , newScore);
                showRequestResult(request);
            }
        }
    }

    public void registerProtestAnswer(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        Score score = table.getSelectionModel().getSelectedItem();
        if (score.getProtest() == null) {
            return;
        }
        if (protestAnswerArea.getText() != null) {
            Request request = new Request(RequestType.REGISTER_PROTEST_ANSWER);
            request.addData("score", score);
            request.addData("protestAnswer", protestAnswerArea.getText());
            showRequestResult(request);
        }
    }

    public void registerAllScores(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REGISTER_ALL_SCORES);
        for (int i = 1; i < updatedScores.size(); i++) {
            if (updatedScores.get(i).getScore() == null) {
                String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty("nullScores");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
                return;
            }
            request.addData("score" + i, updatedScores.get(i));
        }
        showRequestResult(request);
    }

    public void finalizeScores(ActionEvent actionEvent) {
        if (checkNullItems()) return;
        for (Score score : scores) {
            if (score.getScore() == null) {
                if (registerResult) {
                    String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty("finalizeScores");
                    AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
                    return;
                }
            }
        }
        Request request = new Request(RequestType.FINALIZE_SCORES);
        request.addData("lessonCode", lessonCodeField);
        showRequestResult(request);
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
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
        if (lessonCodeField.getText() == null) {
            String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty("nullItem");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
            return false;
        }
        return true;
    }

    private boolean hadScore() {
        for (Score score : scores) {
            if ( Double.parseDouble(score.getScore()) > 0.0) return true;
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTable();
    }
}
