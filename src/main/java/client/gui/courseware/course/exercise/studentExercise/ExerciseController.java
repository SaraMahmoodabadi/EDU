package client.gui.courseware.course.exercise.studentExercise;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Exercise;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.media.MediaHandler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ExerciseController implements Initializable {
    @FXML
    protected Text exerciseName;
    @FXML
    protected TextArea descriptionArea;
    @FXML
    protected Label openLabel;
    @FXML
    protected Label closeLabel;
    @FXML
    protected Label uploadLabel;
    @FXML
    protected Label submissionLabel;
    @FXML
    protected Label scoreLabel;
    @FXML
    protected TextArea answerArea;
    @FXML
    protected Label validLabel;
    @FXML
    protected TextField fileField;
    @FXML
    protected Button addFileButton;
    @FXML
    protected Button registerTextButton;
    @FXML
    protected Button registerFileButton;
    private String courseCode;
    private String exerciseCode;
    private String exerciseFile;

    @FXML
    public void downloadExercise(ActionEvent event) {
        MediaHandler handler = new MediaHandler();
        byte[] file = handler.decode(exerciseFile);
        String path = "src/main/java/client/resource/sentFiles/" + handler.getName();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(file);
            Desktop d = Desktop.getDesktop();
            d.open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        File currentFile = new File(path);
        currentFile.deleteOnExit();
    }

    @FXML
    public void addFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            String path = file.getAbsolutePath();
            fileField.setText(path);
        }
    }

    @FXML
    public void registerFile(ActionEvent event) {
        String path = fileField.getText();
        MediaHandler handler = new MediaHandler();
        String file = handler.encode(path);
        int n = path.split("\\.").length;
        String fileFormat = path.split("\\.")[n-1];
        Request request = new Request(RequestType.SEND_EXERCISE_ANSWER);
        request.addData("exerciseCode", exerciseCode);
        request.addData("type", ItemType.valueOf(validLabel.getText()));
        request.addData("answer", file);
        request.addData("fileFormat", fileFormat);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void registerText(ActionEvent event) {
        String text = answerArea.getText();
        Request request = new Request(RequestType.SEND_EXERCISE_ANSWER);
        request.addData("exerciseCode", exerciseCode);
        request.addData("type", ItemType.valueOf(validLabel.getText()));
        request.addData("answer", text);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        Request request = new Request(RequestType.SHOW_COURSE);
        request.addData("courseCode", this.courseCode);
        ServerController.request = request;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
    }

    private void showData(Map<String, Object> data) {
        Exercise exercise = (Exercise) data.get("exercise");
        exerciseName.setText(exercise.getName());
        openLabel.setText(exercise.getOpeningTime());
        closeLabel.setText(exercise.getClosingTime());
        uploadLabel.setText(exercise.getUploadingTimeWithoutDeductingScores());
        validLabel.setText(exercise.getItemType().toString());
        hide(exercise.getItemType());
        descriptionArea.setText(exercise.getDescriptions());
        exerciseFile = exercise.getFileAddress();
        String submissionStatus = (String) data.get("submissionStatus");
        submissionLabel.setText(submissionStatus);
        String score = (String) data.get("score");
        scoreLabel.setText(score);
    }

    private void hide(ItemType type) {
        if (type == ItemType.TEXT) {
            fileField.setVisible(false);
            addFileButton.setVisible(false);
            addFileButton.setDisable(true);
            registerFileButton.setVisible(false);
            registerFileButton.setDisable(true);
        }
        else {
            answerArea.setVisible(false);
            registerTextButton.setVisible(false);
            registerTextButton.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        this.exerciseCode = (String) request.getData("exerciseCode");
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            showData(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }
}
