package client.gui.courseware.course.exercise.professorExercise;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ExerciseController implements Initializable {
    @FXML
    protected TableView<?> table;
    @FXML
    protected TableColumn<?, ?> nameColumn;
    @FXML
    protected TableColumn<?, ?> codeColumn;
    @FXML
    protected TableColumn<?, ?> timeColumn;
    @FXML
    protected TableColumn<?, ?> scoreColumn;
    @FXML
    protected TableColumn<?, ?> answerColumn;
    @FXML
    protected Text exerciseName;
    @FXML
    protected TextField studentField;
    @FXML
    protected TextField scoreField;
    @FXML
    protected TextArea answerArea;
    private String courseCode;
    private String exerciseCode;
    private boolean isAssistant;
    private boolean stop;
    private String selectedStudent;


    @FXML
    public void registerScore(ActionEvent event) {
        if (selectedStudent == null) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullChoice");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        if (scoreField.getText() == null) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullItem");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        if (!isValid()) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "invalidScore");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        Request request = new Request(RequestType.REGISTER_EXERCISE_SCORE);
        request.addData("studentCode", selectedStudent);
        request.addData("score", scoreField.getText());
        request.addData("exerciseCode", exerciseCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void selectStudent(MouseEvent mouseEvent) {

    }

    @FXML
    public void back(ActionEvent event) {
        Request request = new Request(RequestType.SHOW_COURSE);
        request.addData("courseCode", this.courseCode);
        ServerController.request = request;
        this.stop = true;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
    }

    private boolean isValid() {
        try {
            double score = Double.parseDouble(scoreField.getText());
            if (score < 0.0 || score > 20.0) return false;
        }
        catch (NumberFormatException ignored) {}
        return true;
    }

    private void showData(Map<String, Object> data) {

    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_EXERCISE_PROFESSOR);
                        request.addData("courseCode", this.courseCode);
                        request.addData("exerciseCode", this.exerciseCode);
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            showData(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stop = false;
        selectedStudent = null;
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        this.exerciseCode = (String) request.getData("exerciseCode");
        this.isAssistant = (boolean) request.getData("isAssistant");
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            this.exerciseName.setText((String) response.getData("name"));
            showData(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }
}
