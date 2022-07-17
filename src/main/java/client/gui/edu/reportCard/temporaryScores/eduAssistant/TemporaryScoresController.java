package client.gui.edu.reportCard.temporaryScores.eduAssistant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class TemporaryScoresController implements Initializable {

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
    protected TableView<?> table;
    @FXML
    protected TableColumn<?, ?> lessonCodeColumn;
    @FXML
    protected TableColumn<?, ?> professorCodeColumn;
    @FXML
    protected TableColumn<?, ?> studentCodeColumn;
    @FXML
    protected TableColumn<?, ?> scoreColumn;
    @FXML
    protected TableColumn<?, ?> protestColumn;
    @FXML
    protected TableColumn<?, ?> protestAnswerColumn;
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

    public void showLessonScores(ActionEvent actionEvent) {
    }

    public void showStudentScores(ActionEvent actionEvent) {
    }

    public void showProfessorScores(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
