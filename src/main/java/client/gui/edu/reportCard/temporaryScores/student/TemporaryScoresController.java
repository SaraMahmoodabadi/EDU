package client.gui.edu.reportCard.temporaryScores.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
    protected Rectangle smallRectangle;
    @FXML
    protected TableView<String> table;
    @FXML
    protected TableColumn<String, String> nameColumn;
    @FXML
    protected TableColumn<String, String> scoreColumn;
    @FXML
    protected TableColumn<String, String> protestColumn;
    @FXML
    protected TableColumn<String, String> protestResultColumn;
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

    public void register(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
