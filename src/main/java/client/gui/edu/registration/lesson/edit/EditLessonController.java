package client.gui.edu.registration.lesson.edit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class EditLessonController implements Initializable {


    @FXML
    protected AnchorPane pane;
    @FXML
    protected Rectangle bigRectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Rectangle rectangle2;
    @FXML
    protected Rectangle rectangle3;
    @FXML
    protected Text addText;
    @FXML
    protected Text editText;
    @FXML
    protected Text removeText;
    @FXML
    protected Text nameText;
    @FXML
    protected TextField nameField;
    @FXML
    protected Text codeText1;
    @FXML
    protected TextField codeField1;
    @FXML
    protected Text gradeText;
    @FXML
    protected ComboBox<String> gradeBox;
    @FXML
    protected Text unitText;
    @FXML
    protected ComboBox<String> unitBox;
    @FXML
    protected Text prerequisiteText;
    @FXML
    protected Text Days1;
    @FXML
    protected CheckBox saturday1;
    @FXML
    protected CheckBox sunday1;
    @FXML
    protected CheckBox monday1;
    @FXML
    protected CheckBox tuesday1;
    @FXML
    protected CheckBox wednesday1;
    @FXML
    protected CheckBox thursday1;
    @FXML
    protected Text timeText1;
    @FXML
    protected ComboBox<String> timeBox1;
    @FXML
    protected Text examText1;
    @FXML
    protected DatePicker date1;
    @FXML
    protected ComboBox<String> examHour1;
    @FXML
    protected ListView<String> prerequisiteList;
    @FXML
    protected Button plusButton1;
    @FXML
    protected ImageView plusImage1;
    @FXML
    protected Text needText;
    @FXML
    protected ListView<String> needList;
    @FXML
    protected Button plusButton2;
    @FXML
    protected ImageView plusImage2;
    @FXML
    protected Text professorText1;
    @FXML
    protected TextField professorField1;
    @FXML
    protected Text capacityText1;
    @FXML
    protected TextField capacityField1;
    @FXML
    protected Button register;
    @FXML
    protected Text codeText2;
    @FXML
    protected TextField codeField2;
    @FXML
    protected Line line1;
    @FXML
    protected Text groupText1;
    @FXML
    protected Text professorText2;
    @FXML
    protected TextField professorField2;
    @FXML
    protected Text capacityText2;
    @FXML
    protected TextField capacityField2;
    @FXML
    protected Line line2;
    @FXML
    protected Text TimeOfClassText;
    @FXML
    protected Text Days2;
    @FXML
    protected CheckBox saturday2;
    @FXML
    protected CheckBox sunday2;
    @FXML
    protected CheckBox monday2;
    @FXML
    protected CheckBox tuesday2;
    @FXML
    protected CheckBox wednesday2;
    @FXML
    protected CheckBox thursday2;
    @FXML
    protected Text timeText2;
    @FXML
    protected ComboBox<String> timeBox2;
    @FXML
    protected Line line3;
    @FXML
    protected Text examText2;
    @FXML
    protected DatePicker date2;
    @FXML
    protected ComboBox<String> examHour2;
    @FXML
    protected Button edit;
    @FXML
    protected Text codeText3;
    @FXML
    protected TextField codeField3;
    @FXML
    protected Text groupText2;
    @FXML
    protected TextField groupField;
    @FXML
    protected Button removeLesson;
    @FXML
    protected Button removeGroup;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;


    public void register(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void removeLesson(ActionEvent actionEvent) {
    }

    public void removeGroup(ActionEvent actionEvent) {
    }

    public void back(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
