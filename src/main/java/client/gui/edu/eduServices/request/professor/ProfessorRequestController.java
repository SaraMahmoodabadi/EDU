package client.gui.edu.eduServices.request.professor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class ProfessorRequestController {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Label recommendationLabel;
    @FXML
    protected Label passedLessonsLabel;
    @FXML
    protected Label scoresLabel;
    @FXML
    protected Label assistantLabel;
    @FXML
    protected TextField studentCode;
    @FXML
    protected TextField passedLessonsField;
    @FXML
    protected TextField scoresField;
    @FXML
    protected TextField assistantField;
    @FXML
    protected ComboBox<String> requestBox;
    @FXML
    protected RadioButton accept;
    @FXML
    protected RadioButton reject;
    @FXML
    protected Button registerRecommendation;
    @FXML
    protected Button register;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;

    public void registerRecommendation(ActionEvent actionEvent) {
    }

    public void register(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }
}
