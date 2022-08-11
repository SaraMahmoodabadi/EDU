package client.gui.courseware.minPage;

import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CoursesController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected VBox coursesPane;
    @FXML
    protected DatePicker date;
    @FXML
    protected TextArea events;
    private boolean stop;

    @FXML
    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void updateData() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        updateData();
    }
}
