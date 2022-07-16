package client.gui.edu.registration.professor.list;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfessorListController implements Initializable {

    @FXML
    protected Pane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TextField codeField;
    @FXML
    protected ComboBox<String> degreeBox;
    @FXML
    protected ComboBox<String> collegeBox;
    @FXML
    protected javafx.scene.control.Button show;
    @FXML
    protected javafx.scene.control.Button edit;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    TableView<String> list;
    @FXML
    TableColumn<String, String> nameColumn;
    @FXML
    TableColumn<String, String> collegeColumn;
    @FXML
    TableColumn<String, String> codeColumn;
    @FXML
    TableColumn<String, String> degreeColumn;
    @FXML
    TableColumn<String, String> postColumn;

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
