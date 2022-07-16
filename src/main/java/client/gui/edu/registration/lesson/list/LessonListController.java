package client.gui.edu.registration.lesson.list;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LessonListController implements Initializable {

    @FXML
    protected Pane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TextField lessonCode;
    @FXML
    protected ComboBox<String> unitBox;
    @FXML
    protected ComboBox<String> collegeName;
    @FXML
    protected Button show;
    @FXML
    protected Button edit;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TableView<String> list;
    @FXML
    TableColumn<String, String> codeColumn;
    @FXML
    TableColumn<String, String> groupColumn;
    @FXML
    TableColumn<String, String> unitColumn;
    @FXML
    TableColumn<String, String> nameColumn;
    @FXML
    TableColumn<String, String> prerequisitesColumn;
    @FXML
    TableColumn<String, String> needColumn;
    @FXML
    TableColumn<String, String> capacityColumn;
    @FXML
    TableColumn<String, String> registrationColumn;
    @FXML
    TableColumn<String, String> professorColumn;
    @FXML
    TableColumn<String, String> examTimeColumn;
    @FXML
    TableColumn<String, List<String>> planColumn;


    public void show(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
