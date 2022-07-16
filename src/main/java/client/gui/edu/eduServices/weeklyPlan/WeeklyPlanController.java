package client.gui.edu.eduServices.weeklyPlan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


public class WeeklyPlanController implements Initializable {

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
    protected Rectangle rectangle4;
    @FXML
    protected Rectangle rectangle5;
    @FXML
    protected Rectangle rectangle6;
    @FXML
    protected Rectangle table;
    @FXML
    protected Text saturday;
    @FXML
    protected Text sunday;
    @FXML
    protected Text monday;
    @FXML
    protected Text tuesday;
    @FXML
    protected Text wednesday;
    @FXML
    protected Text thursday;
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
