package client.gui.edu.eduServices.request.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class StudentRequestController {

    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected ComboBox<String> majorBox;
    @FXML
    protected ComboBox<String> requestBox;
    @FXML
    protected Label recentRequestLabel;
    @FXML
    protected TableView<String> table;
    @FXML
    protected TableColumn<String, String> typeColumn;
    @FXML
    protected TableColumn<String, String> dateColumn;
    @FXML
    protected TableColumn<String, String> resultColumn;
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
}
