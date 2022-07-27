package client.gui.edu.reportCard.temporaryScores.student;

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
    protected Rectangle smallRectangle;
    @FXML
    protected TableView<Score> table;
    @FXML
    protected TableColumn<Score, String> codeColumn;
    @FXML
    protected TableColumn<Score, String> scoreColumn;
    @FXML
    protected TableColumn<Score, String> protestColumn;
    @FXML
    protected TableColumn<Score, String> protestResultColumn;
    @FXML
    protected Text explanationText;
    @FXML
    protected TextArea protestArea;
    @FXML
    protected Button register;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    List<Score> scores;

    public void register(ActionEvent actionEvent) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty("nullChoice");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
        }
        else {
            Score score = table.getSelectionModel().getSelectedItem();
            if (score == null || protestArea.getText() == null) {
                String message = Config.getConfig(ConfigType.GUI_TEXT).getProperty("nullItem");
                AlertMonitor.showAlert(Alert.AlertType.ERROR, message);
            }
            else {
                Request request = new Request(RequestType.REGISTER_PROTEST);
                request.addData("score", score);
                request.addData("protest", protestArea.getText());
                showRequestResult(request, score, protestArea.getText());
            }
        }
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void showRequestResult(Request request, Score score, String protest) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            scores.remove(score);
            score.setProtest(protest);
            scores.add(score);
            updateTable(this.scores);
        }
    }

    private void updateTable(List<Score> scores) {
        table.getItems().clear();
        table.getItems().addAll(scores);
    }

    private List<Score> gerData() {
        Request request = new Request(RequestType.SHOW_TEMPORARY_SCORES_PAGE, UserType.STUDENT);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            List<Score> scores = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("score")) {
                    scores.add((Score) V);
                }
            });
            return scores;
        }
        return null;
    }

    private void makeTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("lessonCode"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        protestColumn.setCellValueFactory(new PropertyValueFactory<>("protest"));
        protestResultColumn.setCellValueFactory(new PropertyValueFactory<>("protestAnswer"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTable();
        this.scores = gerData();
        if (scores != null) {
            table.getItems().addAll(scores);
        }
    }
}
