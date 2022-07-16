package client.gui.edu.reportCard.temporaryScores.professor;

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
    protected TableView<String> table;
    @FXML
    protected TableColumn<String, String> studentCodeColumn;
    @FXML
    protected TableColumn<String, String> scoreColumn;
    @FXML
    protected TableColumn<String, String> protestColumn;
    @FXML
    protected TableColumn<String, String> protestAnswerColumn;
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

    public void show(ActionEvent actionEvent) {
    }

    public void registerScore(ActionEvent actionEvent) {
    }

    public void registerProtestAnswer(ActionEvent actionEvent) {
    }

    public void registerAllScores(ActionEvent actionEvent) {
    }

    public void finalizeScores(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
