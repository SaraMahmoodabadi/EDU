package client.gui.edu.eduServices.examList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class ExamListController implements Initializable {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TableView<String> table;
    @FXML
    protected TableColumn<String, String> codeColumn;
    @FXML
    protected TableColumn<String, String> nameColumn;
    @FXML
    protected TableColumn<String, String> timeColumn;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
