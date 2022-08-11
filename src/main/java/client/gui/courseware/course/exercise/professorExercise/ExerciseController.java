package client.gui.courseware.course.exercise.professorExercise;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
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


    @FXML
    public void registerScore(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
