package client.gui.edu.reportCard.eduStatus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class EduStatusController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected TableView<?> table;
    @FXML
    protected TableColumn<?, ?> lessonCodeColumn;
    @FXML
    protected TableColumn<?, ?> scoreColumn;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Text numberPassedText;
    @FXML
    protected Label numberPassedLabel;
    @FXML
    protected Rectangle rectangle4;
    @FXML
    protected Text averageText;
    @FXML
    protected Label averageLabel;

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
