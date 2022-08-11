package client.gui.courseware.course.exercise.studentExercise;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
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

    @FXML
    public void downloadExercise(ActionEvent event) {

    }

    @FXML
    public void addFile(ActionEvent event) {

    }

    @FXML
    public void registerFile(ActionEvent event) {

    }

    @FXML
    public void registerText(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent actionEvent) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
